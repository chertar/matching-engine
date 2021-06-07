package com.chertar;

import javax.sound.midi.Instrument;
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
        OrderBook sameBookSide = order.side().isBuy() ? bids : asks;
        List<Fill> fills = oppositeOrderBook.attemptToFill(order);
        if (order.isFullyFilled()){
            sameBookSide.postOrder(order);
        }
        return fills;
    }
}
