package com.chertar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PriceLevel {
    private final Price price;
    private final List<Order> orders = new ArrayList<>();

    public PriceLevel(Price price) {
        this.price = price;
    }
    public Price price() {
        return price;
    }
    Iterator<Order> orderIterator() {
        return orders.iterator();
    }
}
