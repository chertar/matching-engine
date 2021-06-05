package com.chertar;

import java.math.BigDecimal;

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
