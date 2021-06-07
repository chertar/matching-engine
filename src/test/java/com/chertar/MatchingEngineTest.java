package com.chertar;

import static com.chertar.Side.*;

import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.util.Lists.list;


public class MatchingEngineTest extends TestCase {
    private MatchingEngine engine = new MatchingEngine(Instrument.of("BTC-USD"));

    public void testScenario() {
        assertQuote(0, Double.NaN, 0, Double.NaN);

        // Let's buidl the bid order book
        limit(BUY, 100, 10.0, list());
        assertQuote(100, 10.0, 0, Double.NaN);

        limit(BUY, 50, 10.0, list());
        assertQuote(150, 10.0, 0, Double.NaN);

        limit(BUY, 25, 11.0, list());
        assertQuote(25, 11.0, 0, Double.NaN);

        limit(BUY, 200, 9.0, list());
        assertQuote(25, 11.0, 0, Double.NaN);

        // Now build the asks order book
        limit(SELL, 100, 15.0, list());
        assertQuote(25, 11.0, 100, 15.0);

        limit(SELL, 50, 15.0, list());
        assertQuote(25, 11.0, 150, 15.0);

        limit(SELL, 40, 14.0, list());
        assertQuote(25, 11.0, 40, 14.0);

        limit(SELL, 300, 16.0, list());
        assertQuote(25, 11.0, 40, 14.0);


    }

    private Fill fill(long qty, double price) {
        return Fill.from(price, qty);
    }

    public static Order market(Side side,  long qty) {
        return new Order(side, OrderType.MARKET, qty, Double.NaN);
    }

    public static Quote quote(long qty, double price) {
        return new Quote(Price.of(price), qty);
    }
    private List<Fill> limit(Side side, long qty, double price, List<Fill> expectedFills) {
        return engine.incomingOrder(new Order(side, OrderType.LIMIT, qty, price));
    }

    private List<Fill> market(Side side, long qty, double price, List<Fill> expectedFills) {
        return engine.incomingOrder(new Order(side, OrderType.MARKET, qty, price));
    }

    private void assertQuote(long bidQty, double bidPrice, long askQty, double askPrice) {
        assertThat(engine.topBids()).isEqualTo(Quote.from(bidQty, bidPrice));
        assertThat(engine.topAsks()).isEqualTo(Quote.from(askQty, askPrice));
    }
}