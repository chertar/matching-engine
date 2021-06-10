package com.chertar;

import com.chertar.util.Instrument;
import com.chertar.util.MatchingEngineException;
import com.chertar.util.OrderType;
import com.chertar.util.Side;

import java.util.*;

/**
 * TThe entry-point into the Matching Engine. Provides a way to submit orders,
 * receive fills and query top of book of book market data.  The engine supports Limit and Market orders.
 *
 * Time complexity
 *  - matching time complexity O(n) where "n" is the number of fills.
 *  - posting time complexity is O(1)
 *
 * The pricing engine support a single instrument.  So three matching engine instances would
 * be created to support "BTC-USD", "ETH-USD", "ETH-BTC"
 *
 * This simple implementation does not currently support amending orders, but it
 * can be extended to do so.
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
        if (!order.instrument().equals(this.instrument)) {
            throw new MatchingEngineException("Instrument does not match");
        }
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
    public void cancel(Order order) {
        OrderBook book = order.side().isBuy() ? bids : asks;
        book.cancel(order);
    }
}
