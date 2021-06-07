package com.chertar.util;

/**
 * An abstraction of the financial instrument being traded, which could be of any asset type.
 */
public class Instrument {
    private final String symbol;

    private Instrument(String symbol) {
        this.symbol = symbol;
    }
    public static Instrument of(String symbol) {
        return new Instrument(symbol);
    }
}
