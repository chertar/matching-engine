package com.chertar;

public class Instrument {
    private final String symbol;

    private Instrument(String symbol) {
        this.symbol = symbol;
    }
    public static Instrument of(String symbol) {
        return new Instrument(symbol);
    }
}
