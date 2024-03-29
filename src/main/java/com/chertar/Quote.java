package com.chertar;

import com.chertar.util.Price;

/**
 * Represents the top view of an order book, holding the best bid or ask price
 * and the sum of leaves qty for all orders at that price level.
 * */
public class Quote {
    private final Price price;
    private final long qty;
    private static final Quote nullQuote = from(0, Double.NaN);

    public static Quote from(long qty, double price) {
        return new Quote(Price.of(price), qty);
    }

    public static Quote nullQuote() {
        return nullQuote;
    }

    public Quote(Price price, long qty) {
        this.price = price;
        this.qty = qty;
    }

    public Price price() {
        return this.price;
    }

    public long qty() {
        return this.qty;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quote)) {
            return false;
        }
        Quote other = (Quote) o;
        return other.price.equals(price) && other.qty == qty;
    }

    @Override
    public int hashCode() {
        return price.hashCode();
    }

    @Override
    public String toString() {
        return "Quote{" +
                "price=" + price +
                ", qty=" + qty +
                '}';
    }
}