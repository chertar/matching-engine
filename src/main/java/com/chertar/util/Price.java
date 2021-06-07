package com.chertar.util;

/**
 * A representation of an instrument's price that supports decimal points but
 * unlike Java double, can be compared without needing to adjust for precision.
 * This class is used for storing and sorting prices in the order book and is
 * key to reducing time complexity.
 */
public class Price {
    private final long base;
    private static final int DENOMINATOR = 100;
    private final double doubleValue;

    public static Price of(double value) {
        return new Price(value);
    }

    private Price(double price) {
        this.base = (long) (price * DENOMINATOR);
        this.doubleValue = price;
    }

    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Price)) {
            return false;
        }
        Price otherPrice = (Price) o;
        return  otherPrice.base == base;
    }
    @Override
    public int hashCode() {
        return Long.valueOf(base).hashCode();
    }

    long base() {
        return this.base;
    }

    @Override
    public String toString() {
        return String.format("%.02f", doubleValue);
    }
}
