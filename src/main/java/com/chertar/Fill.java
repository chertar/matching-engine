package com.chertar;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Fill{" +
                "price=" + price +
                ", qty=" + qty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fill fill = (Fill) o;
        return qty == fill.qty && price.equals(fill.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, qty);
    }
}
