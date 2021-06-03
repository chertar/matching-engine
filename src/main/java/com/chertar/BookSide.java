package com.chertar;

import jdk.nashorn.internal.runtime.arrays.IteratorAction;

import java.util.*;

public class BookSide {
    private final Side side;
    private final SortedSet<PriceLevel> priceLevels = new TreeSet<>();
    private final Map<Long, PriceLevel> priceLevelMap = new HashMap<>();

    public BookSide(Side side) {
        this.side = side;
    }

    public void processOrder(Order order) {
        Objects.requireNonNull(order);
        if (order.side() != this.side) {
            throw new MatchingEngineException("Order and book sides do not match. orderSide=" + order.side() + " bookSide=" + this.side);
        }
        // Try to match the order
        Iterator<PriceLevel> iterator = priceLevels.iterator();
        while (iterator.hasNext()) {
            PriceLevel level = iterator.next();
            if (order.type() == OrderType.MARKET || order.getLimitPrice().compareTo(level.price()) >= 0) {
                matchOrders(order, level);
            }

        }
    }
    private void matchOrders(Order order, PriceLevel level) {
        Iterator<Order> restingOrders = level.orderIterator();
        while (restingOrders.hasNext()) {
            
        }
    }
}
