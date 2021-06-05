package com.chertar;

import java.util.Comparator;

public class DescendingComparator implements Comparator<Price> {
    @Override
    public int compare(Price o1, Price o2) {
        return (int) (o2.base() - o1.base());
    }
}
