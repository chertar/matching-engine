package com.chertar;

import static com.chertar.util.Side.*;

import com.chertar.util.*;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.util.Lists.list;


public class OrderBookTest extends TestCase {
    private static Instrument instrument = Instrument.of("BTC-USD");
    public void testPosting() {
        // BUYS
        testPermutation(BUY, list(), quote(0, Double.NaN));

        testPermutation(BUY, list(
                limit(BUY, 100, 100.25)),
                quote(100, 100.25));

        testPermutation(BUY, list(
                limit(BUY, 50, 100.25),
                limit(BUY, 100, 100.25)),
                quote(150, 100.25));

        testPermutation(BUY, list(
                limit(BUY, 50, 100.25),
                limit(BUY, 100, 100.25),
                limit(BUY, 25, 101.0)),
                quote(25, 101.0));

        testPermutation(BUY, list(
                limit(BUY, 50, 100.25),
                limit(BUY, 100, 100.25),
                limit(BUY, 25, 101.0),
                limit(BUY, 10, 99.0)),
                quote(25, 101.0));

        // SELLS
        testPermutation(SELL,
                        list(),
                        quote( 0, Double.NaN));

        testPermutation(SELL, list(
                limit(SELL, 100, 100.25)),
                quote(100, 100.25));

        testPermutation(SELL, list(
                limit(SELL, 50, 100.25),
                limit(SELL, 100, 100.25)),
                quote( 150, 100.25));

        testPermutation(SELL, list(
                limit(SELL, 50, 100.25),
                limit(SELL, 100, 100.25),
                limit(SELL, 25, 101.0)),
                quote(150, 100.25));

        testPermutation(SELL, list(
                limit(SELL, 50, 100.25),
                limit(SELL, 100, 100.25),
                limit(SELL, 25, 101.0),
                limit(SELL, 10, 99.0)),
                quote(10, 99.0));
    }

    public void testPostingInvalidOrders() {
        // Try posting a sell order into a bids OrderBook
        {
            Order order = limit(SELL, 10, 100.25);
            OrderBook orderBook = new OrderBook(BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.post(order))
                    .withMessageContaining("Order and book sides don't match");
        }

        //Try posting a buy order into an asks BookSide
        {
            Order order = limit(BUY, 10, 100.25);
            OrderBook orderBook = new OrderBook(SELL);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.post(order))
                    .withMessageContaining("Order and book sides don't match");
        }
        //Try posting a market order
        {
            Order order = market(BUY, 10);
            OrderBook orderBook = new OrderBook(BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.post(order))
                    .withMessageContaining("Market orders cannot be posted");
        }
    }

    public void testMatchingOnPrice() {
        /***************************************
         * INCOMING SELL VS. RESTING BUY
         ***************************************/

        // Exact price
        testPermutation(BUY,
                        list(limit(BUY, 100, 100.25)),
                        limit(SELL, 100, 100.25),
                        list(fill(100, 100.25)),
                        quote(0, Double.NaN));

        // Incoming order price is more aggressive
        testPermutation(BUY,
                list(limit(BUY, 100, 100.25)),
                limit(SELL, 100, 100.10),
                list(fill(100, 100.25)),
                quote(0, Double.NaN));

        // Incoming order price is more passive
        testPermutation(BUY,
                list(limit(BUY, 100, 100.25)),
                limit(SELL, 100, 100.50),
                list(),
                quote(100,100.25));

        /***************************************
         * INCOMING BUY VS. RESTING SELL
         ***************************************/

        // Exact price
        testPermutation(SELL,
                list(limit(SELL, 100, 100.25)),
                limit(BUY, 100, 100.25),
                list(fill(100, 100.25)),
                quote(0, Double.NaN));

        // Incoming order price is more aggressive
        testPermutation(SELL,
                list(limit(SELL, 100, 100.25)),
                limit(BUY, 100, 100.50),
                list(fill(100, 100.25)),
                quote(0, Double.NaN));

        // Incoming order price is more passive
        testPermutation(SELL,
                list(limit(SELL, 100, 100.25)),
                limit(BUY, 100, 100.10),
                list(),
                quote(100,100.25));
    }

    @Test
    public void testMarketOrder() {
        testPermutation(SELL,
                list(limit(SELL, 100, 100.25)),
                market(BUY, 100),
                list(fill(100, 100.25)),
                quote(0, Double.NaN));
    }

    @Test
    public void testMatchingOnQty() {
        // order qty < resting qty
        testPermutation(BUY,
                list(limit(BUY, 100, 100.25)),
                limit(SELL, 30, 100.25),
                list(fill(30, 100.25)),
                quote(70, 100.25));

        // order qty > resting qty
        testPermutation(BUY,
                list(limit(BUY, 100, 100.25)),
                limit(SELL, 150, 100.25),
                list(fill(100, 100.25)),
                quote(0, Double.NaN));
    }

    @Test
    public void testMatchingMultipleFills() {
        testPermutation(BUY,
                list(limit(BUY, 10, 100.25),
                     limit(BUY, 100, 100.25)),
                limit(SELL, 30, 100.25),
                list(fill(10, 100.25),
                     fill(20, 100.25)),
                quote(80, 100.25));
    }

    @Test
    public void testMatchingMultiplePriceLevels() {
        testPermutation(BUY,
                list(limit(BUY, 10, 100.50),
                     limit(BUY, 100, 100.25)),
                limit(SELL, 30, 100.25),
                list(fill(10, 100.50),
                     fill(20, 100.25)),
                quote(80, 100.25));
    }

    private Fill fill(long qty, double price) {
        return Fill.from(price, qty);
    }
    private static void testPermutation(Side side, List<Order> restingOrders, Order incomingOrder, List<Fill> expectedFills, Quote expectedQuote) {
        OrderBook orderBook = new OrderBook(side);
        for (Order order : restingOrders) {
            orderBook.post(order);
        }
        List<Fill> fills = orderBook.match(incomingOrder);
        assertThat(fills).containsExactlyInAnyOrderElementsOf(expectedFills);
        assertThat(orderBook.topQuote()).isEqualTo(expectedQuote);
    }
    private static void testPermutation(Side side, List<Order> orders, Quote expectedQuote) {
        OrderBook orderBook = new OrderBook(side);
        for (Order order : orders) {
            orderBook.post(order);
        }
        Quote quote = orderBook.topQuote();
        assertThat(quote).isEqualTo(expectedQuote);
    }
    public static Order limit(Side side,  long qty, double price) {
        return new Order(instrument, side, OrderType.LIMIT, qty, price);
    }
    public static Order market(Side side,  long qty) {
        return new Order(instrument, side, OrderType.MARKET, qty, Double.NaN);
    }

    public static Quote quote(long qty, double price) {
        return new Quote(Price.of(price), qty);
    }
}