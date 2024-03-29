package com.chertar;

import com.chertar.util.MatchingEngineException;
import com.chertar.util.OrderType;
import com.chertar.util.Price;

import java.util.*;

/**
 * Responsible for keeping maintaining a fifo queue of resting orders and calculating their cumulative
 * qty
 */
public class PriceLevel {
    private final Price price;
    private final Queue<Order> orders = new ArrayDeque<>();

    public PriceLevel(Price price) {
        this.price = price;
    }
    public Price price() {
        return price;
    }
    public void postOrder(Order order) {
        Objects.requireNonNull(order);
        if(order.type() == OrderType.MARKET) {
            throw new MatchingEngineException("Market order cannot be posted");
        }
        if (!order.limitPrice().equals(this.price)) {
            throw new MatchingEngineException("Order and level prices don't match.");
        }
        if (orders.contains(order)) {
            throw new MatchingEngineException("Order is already posted");
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

    public void cancel(Order order) {
        // This is an O(n) implementation where n is the nubmer of orders in the queue
        // In a production implementation, I would add a hash map so order lookup can be
        // done in O(1)
        order.cancel();
        boolean found = orders.remove(order);

        if (!found) {
            throw new MatchingEngineException("No order found " + order);
        }
    }

    public long qty() {
        // This is a O(n) implementation where n is the number of orders in the queue
        // In a production implementation, I would implement it in O(1) by keeping
        // the qty in the object state and incrementing or decrementing it when
        // orders are posted, filled, amended and cancelled
        return this.orders.stream().mapToLong(order -> order.leavesQty()).sum();
    }
}
