package com.chertar.util;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "symbol='" + symbol + '\'' +
                '}';
    }

    public String symbol() {
        return symbol;
    }
}
