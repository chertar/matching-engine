package com.chertar;

public class Fill {
    private final Price price;
    private final long qty;

    private Fill(Price price, long qty) {
        this.price = price;
        this.qty = qty;
    }

    public static Fill of(Price price, long qty) {
        return new Fill(price, qty);
    }
    public static Fill from(double price, long qty) {
        return new Fill(Price.of(price), qty);
    }
    public long qty() {
        return this.qty;
    }
    public Price price() {
        return price;
    }
}
