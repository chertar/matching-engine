package com.chertar;

import java.math.BigDecimal;

public class Price implements Comparable<Price>{
    private final long base;
    private final int DENOMINATOR = 100;
    private final double doubleValue;

    private Price(double price) {
        this.base = (long) price * DENOMINATOR;
        this.doubleValue = price;
    }

    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public int compareTo(Price o) {
        return (int) (base - o.base);
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
    public boolean equalsPrice(Price price) {
        return price.base == this.base;
    }
    public boolean isMoreAggressiveThan(Price otherPrice, Side side) {
        if (side == Side.BUY) {
            return this.base > otherPrice.base;
        }
        else if (side == Side.SELL) {
            return this.base < otherPrice.base;
        }
        else {
            throw new MatchingEngineException("Unsupported side " + side);
        }
    }
}
