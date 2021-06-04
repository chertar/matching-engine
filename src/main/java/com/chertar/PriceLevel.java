package com.chertar;

import java.util.*;

public class PriceLevel {
    private final Price price;
    private final Queue<Order> orders = new ArrayDeque<>();

    public PriceLevel(Price price) {
        this.price = price;
    }
    public Price price() {
        return price;
    }
    public void putOrder(Order order) {
        Objects.requireNonNull(order);
        if(order.type() == OrderType.MARKET) {
            throw new MatchingEngineException("Market order cannot be posted");
        }
        if (!order.limitPrice().equalsPrice(this.price)) {
            throw new MatchingEngineException("Order and level prices don't match.");
        }
        orders.offer(order);
    }

    public Order peek() {
        return orders.peek();
    }
    public Order poll() {
        return orders.poll();
    }
    public int queueSize() {
        return orders.size();
    }
}
