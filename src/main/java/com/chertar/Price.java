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

}
