package com.chertar;

import javax.sound.midi.Instrument;
import java.util.*;

public class MatchingEngine {
    private final Instrument instrument;

    private BookSide bids = new BookSide(Side.BUY);
    private BookSide asks = new BookSide(Side.SELL);

    public MatchingEngine(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
    }

    public List<Fill> incomingOrder(Order order) {
        BookSide oppositeBookSide = order.side().isBuy() ? asks : bids;
        BookSide sameBookSide = order.side().isBuy() ? bids : asks;
        List<Fill> fills = oppositeBookSide.attemptToFill(order);
        if (order.isFullyFilled()){
            sameBookSide.postOrder(order);
        }
        return fills;
    }
}
