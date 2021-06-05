package com.chertar;

import java.util.Comparator;

public class AscendingComparator implements Comparator<Price> {
    @Override
    public int compare(Price o1, Price o2) {
        return (int) (o1.base() - o2.base());
    }
}
