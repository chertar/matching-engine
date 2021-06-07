package com.chertar;

import java.util.*;

public class OrderBook {
    private final Side side;
    private Comparator<Price> priceComparator;

    // We use a sorted set when matching an order so that finding the first order takes O(1)
    // Inserting or removing a new price level will take O(log(n)) that is an acceptable trade-off
    // since we will be reading price levels much more frequently that we would be inserting and removing them
    private final SortedSet<PriceLevel> priceLevelsSorted;

    // We use a hashmap when posting an order so that it can be done in O(1)
    private final Map<Price, PriceLevel> priceLevelsMapped = new HashMap<>();

    public OrderBook(Side side) {
        this.side = side;
        this.priceComparator = side.isBuy() ? PriceComparator.descending() : PriceComparator.ascending();
        Comparator<PriceLevel> levelComparator = new LevelComparator(priceComparator);
         this.priceLevelsSorted= new TreeSet<PriceLevel>(levelComparator);
    }

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
            long fillQty = Math.min(order.qty(), restingOrder.qty());
            final Fill fill = Fill.of(price, fillQty);
            fills.add(fill);
            restingOrder.processFill(fill);
            if (restingOrder.isFullyFilled()) {
                level.poll();
            }
            order.processFill(fill);
            if (order.isFullyFilled()) {
                return fills;
            }
        }
        throw new MatchingEngineException("There should have been at least one fill");
    }
    public Quote topQuote() {
        if (priceLevelsSorted.isEmpty()){
            return Quote.nullQuote();
        }
        PriceLevel level = this.priceLevelsSorted.first();
        Quote quote = new Quote(level.price(), level.qty());
        return quote;
    }
}
