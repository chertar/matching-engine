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

    public void matchNewOrder(Order order) {
        Side side = order.side();
        if (side == Side.BUY) {
            bids.processOrder(order);
        }
        else if (side == Side.SELL) {
            asks.processOrder(order);
        }
        else {
            throw new MatchingEngineException("Unsupoorted side: " + side);
        }
    }
}
