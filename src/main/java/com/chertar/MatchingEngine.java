package com.chertar;

import java.util.*;

/**
 * This class is the entry-point into the Matching Engine and provides a way to submit orders,
 * receive fills and query top of book market data.  The engine supports Limit and Market orders.
 *
 * This simple implementation does not currently support canceling and amending orders, but it
 * can be exended to do so.  A trivial implementation of amending and canceling would be O(n),
 * a more efficient implementation can achieve O(1) but requires additional tracking of order ids,
 * which given the time constraint of the project, I have decided to leave out rather than implement
 * inefficiently.
 */
public class MatchingEngine {
    private final Instrument instrument;

    private OrderBook bids = new OrderBook(Side.BUY);
    private OrderBook asks = new OrderBook(Side.SELL);

    public MatchingEngine(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
    }

    public List<Fill> process(Order order) {
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
