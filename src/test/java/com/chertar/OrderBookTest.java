package com.chertar;

import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.util.Lists.list;


public class OrderBookTest extends TestCase {

    public void testPostingBuyOrders() {
        testPermutation(Side.BUY, list(), sidedQuote(Double.NaN, 0));

        testPermutation(Side.BUY, list(
                limit(Side.BUY, 100, 100.25)),
                sidedQuote(100.25, 100));

        testPermutation(Side.BUY, list(
                limit(Side.BUY, 50, 100.25),
                limit(Side.BUY, 100, 100.25)),
                sidedQuote(100.25, 150));

        testPermutation(Side.BUY, list(
                limit(Side.BUY, 50, 100.25),
                limit(Side.BUY, 100, 100.25),
                limit(Side.BUY, 25, 101.0)),
                sidedQuote(101.0, 25));

        testPermutation(Side.BUY, list(
                limit(Side.BUY, 50, 100.25),
                limit(Side.BUY, 100, 100.25),
                limit(Side.BUY, 25, 101.0),
                limit(Side.BUY, 10, 99.0)),
                sidedQuote(101.0, 25));
    }

    public void testPostingSellOrders() {
        testPermutation(Side.SELL, list(), sidedQuote(Double.NaN, 0));

        testPermutation(Side.SELL, list(
                limit(Side.SELL, 100, 100.25)),
                sidedQuote(100.25, 100));

        testPermutation(Side.SELL, list(
                limit(Side.SELL, 50, 100.25),
                limit(Side.SELL, 100, 100.25)),
                sidedQuote(100.25, 150));

        testPermutation(Side.SELL, list(
                limit(Side.SELL, 50, 100.25),
                limit(Side.SELL, 100, 100.25),
                limit(Side.SELL, 25, 101.0)),
                sidedQuote(100.25, 150));

        testPermutation(Side.SELL, list(
                limit(Side.SELL, 50, 100.25),
                limit(Side.SELL, 100, 100.25),
                limit(Side.SELL, 25, 101.0),
                limit(Side.SELL, 10, 99.0)),
                sidedQuote(99.0, 10));
    }

    public void testPostingInvalidOrders() {
        // Try posting a sell order into a buy BookSide
        {
            Order order = limit(Side.SELL, 10, 100.25);
            OrderBook orderBook = new OrderBook(Side.BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.postOrder(order))
                    .withMessageContaining("Order and book sides don't match");
        }

        //Try posting a buy order into a sell BookSide
        {
            Order order = limit(Side.BUY, 10, 100.25);
            OrderBook orderBook = new OrderBook(Side.SELL);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.postOrder(order))
                    .withMessageContaining("Order and book sides don't match");
        }
        //Try posting a market order
        {
            Order order = market(Side.BUY, 10);
            OrderBook orderBook = new OrderBook(Side.BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> orderBook.postOrder(order))
                    .withMessageContaining("Market orders cannot be posted");
        }
    }

    private static void testPermutation(Side side, List<Order> orders, OrderBook.BookQuote expectedQuote) {
        OrderBook orderBook = new OrderBook(side);
        for (Order order : orders) {
            orderBook.postOrder(order);
        }
        OrderBook.BookQuote quote = orderBook.bestBidOffer();
        assertThat(quote).isEqualTo(expectedQuote);
    }

    public void testAttemptToFill() {
        // Exact price and qty
        testPermutation(Side.BUY,
                        list(limit(Side.BUY, 100, 100.25)),
                        limit(Side.SELL, 100, 100.25),
                        list(Fill.from(100.25, 100)),
                        OrderBook.BookQuote.from(0, Double.NaN));

        // Incoming order price is more aggressive
        testPermutation(Side.BUY,
                list(limit(Side.BUY, 100, 100.25)),
                limit(Side.SELL, 100, 100.10),
                list(Fill.from(100.25, 100)),
                OrderBook.BookQuote.from(0, Double.NaN));

        // Incoming order price is more passive
        testPermutation(Side.BUY,
                list(limit(Side.BUY, 100, 100.25)),
                limit(Side.SELL, 100, 100.50),
                list(),
                OrderBook.BookQuote.from(100, 100.25));

    }

    private static void testPermutation(Side side, List<Order> restingOrders, Order incomingOrder, List<Fill> expectedFills, OrderBook.BookQuote expectedQuote) {
        OrderBook orderBook = new OrderBook(side);
        for (Order order : restingOrders) {
            orderBook.postOrder(order);
        }
        List<Fill> fills = orderBook.attemptToFill(incomingOrder);
        assertThat(fills).containsExactlyElementsOf(expectedFills);
        assertThat(orderBook.bestBidOffer()).isEqualTo(expectedQuote);
    }

    public static Order limit(Side side,  long qty, double price) {
        return new Order(side, OrderType.LIMIT, qty, price);
    }
    public static Order market(Side side,  long qty) {
        return new Order(side, OrderType.MARKET, qty, Double.NaN);
    }
    public static OrderBook.BookQuote sidedQuote(double price, long qty) {
        return new OrderBook.BookQuote(Price.of(price), qty);
    }
}