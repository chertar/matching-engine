package com.chertar;

import com.chertar.util.*;

/**
 * A simplified view of an order in the system.  Contains the ability to process fills
 * and compute average price.
 */
public class Order {
    private final String id;
    private final Instrument instrument;
    private final Side side;
    private final OrderType type;
    private final Price limitPrice;
    private final long qty;
    // Here I use a simple boolean to keep track of order state
    // In a production implementation I would use an enum and
    // support other states such as "filled", "rejected", etc.
    private boolean canceled = false;

    private Price avgPrice = Price.of(0.0);
    private long filledQty;

    public Order(String id, Instrument instrument, Side side, OrderType type,  long qty, double limitPrice) {
        this.id = id;
        this.instrument = instrument;
        this.side = side;
        this.type = type;
        this.limitPrice = Price.of(limitPrice);
        this.qty = qty;
    }

    public String id() {
        return this.id;
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
    public boolean cancelled() {
        return canceled;
    }

    public Instrument instrument() {
        return instrument;
    }

    public void cancel() {
        if (canceled) {
            throw new MatchingEngineException("Order is already canceled");
        }
        if (type == OrderType.MARKET) {
            throw new MatchingEngineException("Market order cannot be canceled");
        }
        this.canceled = true;
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
        return canceled ? 0 : qty - filledQty;
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
                "id=" + id +
                " side=" + side +
                ", type=" + type +
                ", limitPrice=" + limitPrice +
                ", qty=" + qty +
                ", filledQty=" + filledQty +
                ", canceled=" + canceled +
                ", leaves=" + leavesQty() +
                '}';
    }

}
