package com.chertar;

import com.chertar.util.*;

public class Order {
    private final Instrument instrument;
    private final Side side;
    private final OrderType type;
    private final Price limitPrice;
    private final long qty;

    private Price avgPrice = Price.of(0.0);
    private long filledQty;

    public Order(Instrument instrument, Side side, OrderType type,  long qty, double limitPrice) {
        this.instrument = instrument;
        this.side = side;
        this.type = type;
        this.limitPrice = Price.of(limitPrice);
        this.qty = qty;
    }

    public Side side() {
        return side;
    }
    public OrderType type() {
        return type;
    }
    public Price limitPrice() {
        return limitPrice;
    }
    public long qty() {
        return this.qty;
    }

    public Instrument instrument() {
        return instrument;
    }

    public void processFill(Fill fill) {
        if (fill.qty() > leavesQty()) {
            throw new MatchingEngineException("Order is overfilled");
        }
        //Update qty
        long previousFilledQty = this.filledQty;
        filledQty += fill.qty();

        //Update avg fill price
        double previousAvgPrice = avgPrice.doubleValue();
        double newAvgPrice = (previousAvgPrice * previousFilledQty + fill.price().doubleValue() * fill.qty())
                             / (previousFilledQty + fill.qty());
        this.avgPrice = Price.of(newAvgPrice);
    }

    public boolean isFullyFilled() {
        return qty == filledQty;
    }
    public long leavesQty() {
        return qty - filledQty;
    }
    public long filledQty() {
        return filledQty;
    }

    public Price avgPrice() {
        return avgPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "side=" + side +
                ", type=" + type +
                ", limitPrice=" + limitPrice +
                ", qty=" + qty +
                ", filledQty=" + filledQty +
                '}';
    }

}
