package com.chertar;

import java.util.*;

public class BookSide {
    private final Side side;
    private final SortedSet<PriceLevel> priceLevels = new TreeSet<>();
    private final Map<Long, PriceLevel> priceLevelMap = new HashMap<>();

    public BookSide(Side side) {
        this.side = side;
    }

    public List<Fill> attemptToFill(Order order) {
        Objects.requireNonNull(order);
        if (order.side() != this.side) {
            throw new MatchingEngineException("Order and book sides do not match. orderSide=" + order.side() + " bookSide=" + this.side);
        }
        // Try to match the order
        Iterator<PriceLevel> iterator = priceLevels.iterator();
        List<Fill> fills = new ArrayList<>();
        while (iterator.hasNext()) {
            PriceLevel level = iterator.next();
            if (order.type() == OrderType.MARKET ) {
                List<Fill> newFills = generateFills(order, level, level.price());
                fills.addAll(newFills);
            }
            else if (order.type() == OrderType.LIMIT) {
                if (order.limitPrice().equals(level.price())) {
                    List<Fill> newFills = generateFills(order, level, level.price());
                    fills.addAll(newFills);
                }
                else if (order.limitPrice().isMoreAggressiveThan(level.price(), side)) {
                    List<Fill> newFills = generateFills(order, level, level.price());
                    fills.addAll(newFills);
                }
                else {
                    break;
                }
            }
        }
        return fills;
    }

    private List<Fill> generateFills(Order order, PriceLevel level, Price price) {
        List<Fill> fills = new ArrayList<>();
        Iterator<Order> restingOrders = level.orderIterator();
        while (restingOrders.hasNext()) {
            Order restingOrder = restingOrders.next();
            long fillQty = Math.min(order.qty(), restingOrder.qty());
            final Fill fill = Fill.of(price, fillQty);
            fills.add(fill);
            restingOrder.processFill(fill);
            order.processFill(fill);
            if (order.isFullyFilled()) {
                return fills;
            }
        }
        throw new MatchingEngineException("There should have been at least one fill");
    }
}
