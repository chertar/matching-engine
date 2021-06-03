package com.chertar;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class PriceTest extends TestCase {

    public void testDoubleValue() {
        double price = 100.02;
        assertThat(Price.of(price).doubleValue()).isCloseTo(price, within(0.001));
    }

    public void testCompareTo() {
        Price small = Price.of(100.01);
        Price big = Price.of(100.02);
        assertThat(small.compareTo(big)).isNegative();
        assertThat(big.compareTo(small)).isPositive();
        assertThat(small.compareTo(small)).isZero();
    }

    public void testEquals() {
        assertThat(Price.of(100.25)).isNotEqualTo(Price.of(100.24));
        assertThat(Price.of(100.25)).isEqualTo(Price.of(100.25));
        assertThat(Price.of(100.25)).isNotEqualTo(Price.of(100.26));
    }

    public void testTestHashCode() {
        assertThat(Price.of(100.25).hashCode()).isEqualTo(Long.valueOf(10025).hashCode());
    }

    public void testEqualsPrice() {
        assertThat(Price.of(100.25).equalsPrice(Price.of(100.24))).isFalse();
        assertThat(Price.of(100.25).equalsPrice(Price.of(100.25))).isTrue();
        assertThat(Price.of(100.25).equalsPrice(Price.of(100.26))).isFalse();
    }

    public void testIsMoreAggressiveThan() {
        Price small = Price.of(100.01);
        Price big = Price.of(100.02);

        assertThat(small.isMoreAggressiveThan(big, Side.SELL)).isTrue();
        assertThat(small.isMoreAggressiveThan(big, Side.BUY)).isFalse();

        assertThat(big.isMoreAggressiveThan(small, Side.SELL)).isFalse();
        assertThat(big.isMoreAggressiveThan(small, Side.BUY)).isTrue();
    }
}