package com.chertar;

import javax.sound.midi.Instrument;
import java.util.*;

public class MatchingEngine {
    private final Instrument instrument;

    private BookSide bids = new BookSide(side);
    private BookSide asks = new BookSide(side);

    private final SortedSet<PriceLevel> bidSet = new TreeSet<>();
    private final Map<Long, PriceLevel> bidMap = new HashMap<>();




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
