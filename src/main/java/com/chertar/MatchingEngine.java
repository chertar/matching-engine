package com.chertar;

import javax.sound.midi.Instrument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MatchingEngine {
    private final Instrument instrument;
    private final Map<Long, List<Order>> bids = new HashMap<>();
    private final Map<Long, List<Order>> asks = new HashMap<>();

    public MatchingEngine(Instrument instrument) {
        Objects.requireNonNull(instrument);
        this.instrument = instrument;
    }

    public void matchNewOrder(Order order) {
        Side side = order.side();
        if (side == Side.BUY) {
            processBid(order);
        }
        else if (side == Side.SELL) {
            //processOffer(order);
        }
        else {
            throw new MatchingEngineException("Unsupoorted side: " + side);
        }
    }

    private void processBid(Order order) {
        if (order.type() == OrderType.LIMIT) {
            // Check if order matches any asks
            

            // Post the order if no match is found
        }
        throw new MatchingEngineException("Unsupported order type: " + order.type());
    }
}
