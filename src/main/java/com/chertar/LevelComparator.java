package com.chertar;

import java.util.Comparator;

public class LevelComparator implements Comparator<PriceLevel> {
    private final Comparator<Price> comparator;

    public LevelComparator(Comparator<Price> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(PriceLevel o1, PriceLevel o2) {
        return comparator.compare(o1.price(), o2.price());
    }
}
