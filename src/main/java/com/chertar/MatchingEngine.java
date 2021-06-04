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

    public List<Fill> attemptToFill(Order order) {
        Side side = order.side();
        if (side == Side.BUY) {
            List<Fill> fills =  asks.attemptToFill(order);
            if (!order.isFullyFilled()) {
                bids.postOrder(order);
            }
            return fills;
        }
        else if (side == Side.SELL) {
            List<Fill> fills = bids.attemptToFill(order);
            if (!order.isFullyFilled()) {
                asks.postOrder(order);
            }
            return fills;
        }
        else {
            throw new MatchingEngineException("Unsupoorted side: " + side);
        }
    }
    public void post(Order order) {
        // TODO
    }
}
