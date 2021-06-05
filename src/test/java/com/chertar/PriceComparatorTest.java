package com.chertar;

import junit.framework.TestCase;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public class PriceComparatorTest extends TestCase {

    public void testAscending() {
        Comparator<Price> comparator = PriceComparator.ascending();
        assertThat(comparator.compare(Price.of(100.0), Price.of(100.01))).isNegative();
        assertThat(comparator.compare(Price.of(100.0), Price.of(100.0))).isZero();
        assertThat(comparator.compare(Price.of(100.0), Price.of(99.99))).isPositive();
    }

    public void testDescending() {
        Comparator<Price> comparator = PriceComparator.descending();
        assertThat(comparator.compare(Price.of(100.0), Price.of(100.01))).isPositive();
        assertThat(comparator.compare(Price.of(100.0), Price.of(100.0))).isZero();
        assertThat(comparator.compare(Price.of(100.0), Price.of(99.99))).isNegative();
    }
}