package com.chertar;

public class Order {
    private Side side;
    private OrderType type;
    private Price limitPrice;
    private long qty;
    private long filledQty;
    private Price avgPrice;
    public Side side() {
        return side;
    }
    public OrderType type() {
        return type;
    }
    public Price getLimitPrice() {
        return limitPrice;
    }
    public long qty() {
        return this.qty;
    }

    public void processFill(Fill fill) {
        long previousFilledQty = this.filledQty;
        filledQty += fill.qty();
    }

    public boolean isFullyFilled() {
        return qty == filledQty;
    }
}
