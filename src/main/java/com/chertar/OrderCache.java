package com.chertar;

import com.chertar.util.MatchingEngineException;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache which allows orders to be looked up by id in constant time
 */
public class OrderCache {
    private final Map<String, Order> orders = new HashMap<>();

    public Order get(String id) {
        return orders.get(id);
    }
    public void add(Order order){
        Order prexisting = orders.put(order.id(), order);
        if (prexisting != null) {
            if (order == prexisting) {
                throw new MatchingEngineException("This order is already in the cache");
            }
            throw new MatchingEngineException("There is a prexisting order with this id");
        }
    }
    // In the current implementation, orders never get removed from the map
    // which overtime results in a memory leak.  In a production implementation
    // I would remove orders when they are fully filled or cancelled
    public Order remove(String id) {
        Order order = get(id);
        if (order == null) {
            throw new MatchingEngineException("No order found with id '"+id+"'");
        }
        return orders.remove(order);
    }
}
