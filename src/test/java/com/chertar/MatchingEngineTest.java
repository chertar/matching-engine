package com.chertar;

import static com.chertar.util.Side.*;

import com.chertar.util.*;
import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;


public class MatchingEngineTest extends TestCase {
    private Instrument instrument = Instrument.of("BTC-USD");
    private MatchingEngine engine = new MatchingEngine(instrument);
    private static OrderIdGenerator idGenerator = new OrderIdGenerator();

    public void testBuyLimitOrders() {
        buildOrderBook();

        /* Expected order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30      15.0
         200        9.0         300         16.0    */

        // First order, should receive partial fill and post
        limit(BUY, 500, 15.0, list(
                fill(40, 14.0),
                fill(80, 15.0),
                fill(30, 15.0)));

        // Verify posting is correct.  Of the 500 qty, 150 was filled, so the rest (350) should be posted to bids
        assertQuote(350, 15.0, 300, 16.0);

        /* Expected orderbook
         bidQty     bidPrice    askQty      askPrice
         350        15.0        300         16.0
         25         11.0
         100, 50    10.0
         200        9.0                                      */

        // Second order should be fully filled
        limit(BUY, 10, 16.0, list(
                fill(10, 16.0)));

        /* Expected orderbook
         bidQty     bidPrice    askQty      askPrice
         350        15.0        290         16.0
         25         11.0
         100, 50    10.0
         200        9.0                                      */

        // Verify that quote was depleted and no bid was posted
        assertQuote(350, 15.0, 290, 16.0);

    }

    public void testSellLimitOrder() {
        buildOrderBook();

        /* Expeceted order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30      15.0
         200        9.0         300         16.0    */

        // First order, should receive partial fill and post
        limit(SELL, 500, 10.0, list(
                fill(25, 11.0),
                fill(100, 10.0),
                fill(50, 10.0)));

        /* Expeceted order book
         bidQty     bidPrice    askQty      askPrice
         200        9.0         325         10.0
                                40          14.0
                                80, 30      15.0    */

        // Verify posting is correct.  Of the 500 qty, 175 was filled, so the rest (325) should be posted to bids
        assertQuote(200, 9.0, 325, 10.0);

        // Second order should be fully filled
        limit(SELL, 20, 9.0, list(
                fill(20, 9.0)));

        /* Expeceted order book
         bidQty     bidPrice    askQty      askPrice
         180        9.0         325         10.0
                                40          14.0
                                80, 30      15.0    */

        // Verify that quote was depleted and no bid was posted
        assertQuote(180, 9.0, 325, 10.0);
    }

    public void testBuyMarketOrders() {
        buildOrderBook();

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30      15.0
         200        9.0         300         16.0     */

        // First order, depletes two price levels
        market(BUY, 130, list(
                fill(40, 14.0),
                fill(80, 15.0),
                fill(10, 15.0)));

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        20          15.0
         100, 50    10.0        300         16.0
         200        9.0                                 */

        assertQuote(25, 11.0, 20, 15.0);

        // Second order should deplete the order book and not post
        market(BUY, 1000, list(
                fill(20, 15.0),
                fill(300, 16.0)));

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        0           NaN
         100, 50    10.0
         200        9.0                                 */


        // Verify that quote was depleted and no bid was posted
        assertQuote(25, 11.0, 0, Double.NaN);
    }

    public void testSellMarketOrders() {
        buildOrderBook();

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30      15.0
         200        9.0         300         16.0     */

        // First order, depletes two price levels
        market(SELL, 100, list(
                fill(25, 11.0),
                fill(75, 10.0)));

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         25, 50     10.0        40          14.0
         200        9.0         80, 30      15.0
                                300         16.0     */

        assertQuote(75, 10.0, 40, 14.0);

        // Second order should deplete the order book and not post
        market(SELL, 1000, list(
                fill(25, 10.0),
                fill(50, 10.0),
                fill(200, 9.0)));

        /*
        Expected order book
         bidQty     bidPrice    askQty      askPrice
         0          NaN         40          14.0
                                80, 30      15.0
                                300         16.0     */

        assertQuote(0, Double.NaN, 40, 14.0);
    }

    public void testCancelingBuyOrder() {
        buildOrderBook();

        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(25, 11.0, 40, 14.0);

        Order order = new Order(idGenerator.next(), instrument, BUY, OrderType.LIMIT, 10, 11.0);
        engine.newOrder(order);
        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25, 10     11.0        40          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(35, 11.0, 40, 14.0);

        engine.cancel(order);
        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(25, 11.0, 40, 14.0);
    }

    public void testCancelingSellOrder() {
        buildOrderBook();

        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(25, 11.0, 40, 14.0);

        Order order = new Order(idGenerator.next(), instrument, SELL, OrderType.LIMIT, 10, 14.0);
        engine.newOrder(order);
        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25, 10     11.0        40, 10          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(25, 11.0, 50, 14.0);

        engine.cancel(order);
        /** expected order book:
         bidQty     bidPrice    askQty      askPrice
         25         11.0        40          14.0
         100, 50    10.0        80, 30     15.0
         200        9.0         300         16.0
         */
        assertQuote(25, 11.0, 40, 14.0);
    }

    /** creates order book:
     bidQty     bidPrice    askQty      askPrice
     25         11.0        40          14.0
     100, 50    10.0        80, 30     15.0
     200        9.0         300         16.0
     */
    private void buildOrderBook() {
        assertQuote(0, Double.NaN, 0, Double.NaN);
        // Buys
        limit(BUY, 100, 10.0, list());
        assertQuote(100, 10.0, 0, Double.NaN);

        limit(BUY, 50, 10.0, list());
        assertQuote(150, 10.0, 0, Double.NaN);

        limit(BUY, 25, 11.0, list());
        assertQuote(25, 11.0, 0, Double.NaN);

        limit(BUY, 200, 9.0, list());
        assertQuote(25, 11.0, 0, Double.NaN);

        // Sells
        limit(SELL, 80, 15.0, list());
        assertQuote(25, 11.0, 80, 15.0);

        limit(SELL, 30, 15.0, list());
        assertQuote(25, 11.0, 110, 15.0);

        limit(SELL, 40, 14.0, list());
        assertQuote(25, 11.0, 40, 14.0);

        limit(SELL, 300, 16.0, list());
        assertQuote(25, 11.0, 40, 14.0);
    }

    private Fill fill(long qty, double price) {
        return Fill.from(price, qty);
    }

    public static Order market(Side side, long qty) {
        return new Order(idGenerator.next(), Instrument.of("BTC-USD"),side, OrderType.MARKET, qty, Double.NaN);
    }

    public static Quote quote(long qty, double price) {
        return new Quote(Price.of(price), qty);
    }
    private void limit(Side side, long qty, double price, List<Fill> expectedFills) {
        submitOrderAndVerifyFills(side, OrderType.LIMIT, qty, price, expectedFills);
    }

    private void market(Side side, long qty, List<Fill> expectedFills) {
        submitOrderAndVerifyFills(side, OrderType.MARKET, qty, Double.NaN, expectedFills);
    }

    private void submitOrderAndVerifyFills(Side side, OrderType type, long qty, double price, List<Fill> expectedFills) {
        List<Fill> fills = engine.newOrder(new Order(idGenerator.next(), instrument, side, type, qty, price));
        assertThat(fills).containsExactlyElementsOf(expectedFills);
    }

    private void assertQuote(long bidQty, double bidPrice, long askQty, double askPrice) {
        assertThat(engine.topBids()).isEqualTo(Quote.from(bidQty, bidPrice));
        assertThat(engine.topAsks()).isEqualTo(Quote.from(askQty, askPrice));
    }
}