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
        assertThat(Price.of(100.25)).isEqualTo(Price.of(100.25));
        assertThat(Price.of(100.25)).isNotEqualTo(Price.of(100.24));
    }

    public void testTestEquals() {
    }

    public void testTestHashCode() {
    }

    public void testEqualsPrice() {
    }

    public void testIsMoreAggressiveThan() {
    }
}