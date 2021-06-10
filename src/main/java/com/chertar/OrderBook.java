package com.chertar;

import com.chertar.util.*;

import java.util.*;

/**
 * One side of the Matching Engine, either bids or asks.  It maintains a collection Price Levels
 * and contains logic for:
 * - matching an incoming opposite-side order against existing orders
 * - posting a same-side order to the appropriate price leve
 *
 * We store price levels in two data structures at the same time in order to achieve better time-complexity.
 * See in-line comments for more details.
 */
public class OrderBook {
    private final Side side;
    private Comparator<Price> priceComparator;

    // Data structure 1: We use a sorted set when matching an order so that finding the first order takes O(1)
    // Inserting or removing a new price level will take O(log(m)) where m is the number of price levels. It's
    // an desirable trade-off since we will be reading price levels much more frequently that we will be inserting and removing them
    private final SortedSet<PriceLevel> priceLevelsSorted;

    // Data structure 2: We use a hashmap when posting an order so that it can be done in O(1)
    private final Map<Price, PriceLevel> priceLevelsMapped = new HashMap<>();

    public OrderBook(Side side) {
        this.side = side;
        this.priceComparator = side.isBuy() ? PriceComparator.descending() : PriceComparator.ascending();
        Comparator<PriceLevel> levelComparator = new LevelComparator(priceComparator);
         this.priceLevelsSorted= new TreeSet<PriceLevel>(levelComparator);
    }

    /**
     * Adds the passed order the the order book at the appropriate price level
     * @param order
     * @throws MatchingEngine if the order if of a different side, of order type market, or has been fully traded
     */
    public void post(Order order) {
        if (order.side() != this.side) {
            throw new MatchingEngineException("Order and book sides don't match");
        }
        if (order.type() == OrderType.MARKET) {
            throw new MatchingEngineException("Market orders cannot be posted");
        }
        if (order.leavesQty() <=0 ) {
            throw new MatchingEngineException("Cannot post fully traded order");
        }

        Price price = order.limitPrice();
        PriceLevel level = priceLevelsMapped.get(price);
        if (level == null) {
            level = new PriceLevel(order.limitPrice());
            priceLevelsSorted.add(level);
            priceLevelsMapped.put(price, level);
        }
        level.postOrder(order);
    }

    /**
     * Attempts to fill the passed order against prexisting orders
     * @param order
     * @return litst of fills if there was a match
     * @throws MatchingEngine if the order is of the same side as the order book
     */
    public List<Fill> match(Order order) {
        Objects.requireNonNull(order);
        if (order.side().isBuy() == side.isBuy()) {
            throw new MatchingEngineException("Order and book sides do not match. orderSide=" + order.side() + " bookSide=" + this.side);
        }
        Iterator<PriceLevel> iterator = priceLevelsSorted.iterator();
        List<Fill> fills = new ArrayList<>();
        while (iterator.hasNext()) {
            PriceLevel level = iterator.next();
            if (order.type() == OrderType.MARKET
                || (order.type() == OrderType.LIMIT && priceComparator.compare(order.limitPrice(), level.price()) >= 0)) {
                    List<Fill> newFills = generateFills(order, level, level.price());
                    fills.addAll(newFills);
            }
            if (level.queueSize() == 0) {
                iterator.remove();
                priceLevelsMapped.remove(level.price());
            }
            if (order.isFullyFilled()) {
                break;
            }
        }
        return fills;
    }

    private List<Fill> generateFills(Order order, PriceLevel level, Price price) {
        List<Fill> fills = new ArrayList<>();
        while (level.peek() != null) {
            Order restingOrder = level.peek();
            long fillQty = Math.min(order.leavesQty(), restingOrder.leavesQty());
            final Fill fill = Fill.of(price, fillQty);
            fills.add(fill);
            restingOrder.processFill(fill);
            if (restingOrder.isFullyFilled()) {
                level.poll();
            }
            order.processFill(fill);
            if (order.isFullyFilled()) {
                break;
            }
        }
        return fills;
    }

    /**
     * @return the quote of the top price level based on price aggressiveness.
     * Bids return the lowest price level.  Asks return the highest.
     */
    public Quote topQuote() {
        if (priceLevelsSorted.isEmpty()){
            return Quote.nullQuote();
        }
        PriceLevel level = this.priceLevelsSorted.first();
        Quote quote = new Quote(level.price(), level.qty());
        return quote;
    }
    void cancel(Order order) {
        if (order.side() != this.side) {
            throw new MatchingEngineException("Cannot cancel an order iwth a different side");
        }
        PriceLevel level = this.priceLevelsMapped.get(order.limitPrice());
        if (level == null) {
            throw new MatchingEngineException("No price level found for price " + order.limitPrice());
        }
        level.cancel(order);
        if (level.queueSize() == 0) {
            this.priceLevelsMapped.remove(level.price());
            this.priceLevelsSorted.remove(level);
        }
    }
}
