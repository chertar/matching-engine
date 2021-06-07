package com.chertar;

import java.util.*;

public class MatchingEngine {
    private final Instrument instrument;

    private OrderBook bids = new OrderBook(Side.BUY);
    private OrderBook asks = new OrderBook(Side.SELL);

    public MatchingEngine(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
    }

    public List<Fill> incomingOrder(Order order) {
        OrderBook oppositeOrderBook = order.side().isBuy() ? asks : bids;
        OrderBook sameSideOrderBook = order.side().isBuy() ? bids : asks;
        List<Fill> fills = oppositeOrderBook.match(order);
        if (!order.isFullyFilled() && order.type() == OrderType.LIMIT){
            sameSideOrderBook.post(order);
        }
        return fills;
    }
    public Quote topBids() {
        return this.bids.topQuote();
    }
    public Quote topAsks() {
        return this.asks.topQuote();
    }
}
