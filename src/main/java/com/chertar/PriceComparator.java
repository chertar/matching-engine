package com.chertar;

import java.util.Comparator;

public class PriceComparator {
    private static final Comparator<Price> ascending = new Ascending();
    private static final Comparator<Price> descending = new Descending();

    public static Comparator<Price> ascending() {
        return ascending;
    }
    public static Comparator<Price> descending() {
        return descending;
    }
    private static class Ascending implements Comparator<Price> {
        @Override
        public int compare(Price o1, Price o2) {
            return (int) (o1.base() - o2.base());
        }
    }
    private static class Descending implements Comparator<Price> {
        @Override
        public int compare(Price o1, Price o2) {
            return (int) (o2.base() - o1.base());
        }
    }
}
