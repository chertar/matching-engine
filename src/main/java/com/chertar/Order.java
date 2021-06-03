package com.chertar;

public class Order {
    private Side side;
    private OrderType type;
    private Price limitPrice;
    public Side side() {
        return side;
    }
    public OrderType type() {
        return type;
    }
    public Price getLimitPrice() {
        return limitPrice;
    }
}
