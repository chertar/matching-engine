package com.chertar;

import junit.framework.TestCase;
import org.assertj.core.util.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.util.Lists.list;


public class BookSideTest extends TestCase {

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
            BookSide bookSide = new BookSide(Side.BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> bookSide.postOrder(order))
                    .withMessageContaining("Order and book sides don't match");
        }

        //Try posting a buy order into a sell BookSide
        {
            Order order = limit(Side.BUY, 10, 100.25);
            BookSide bookSide = new BookSide(Side.SELL);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> bookSide.postOrder(order))
                    .withMessageContaining("Order and book sides don't match");
        }
        //Try posting a market order
        {
            Order order = market(Side.BUY, 10);
            BookSide bookSide = new BookSide(Side.BUY);
            assertThatExceptionOfType(MatchingEngineException.class)
                    .isThrownBy(() -> bookSide.postOrder(order))
                    .withMessageContaining("Market orders cannot be posted");
        }
    }

    private static void testPermutation(Side side, List<Order> orders, BookSide.SidedQuote expectedQuote) {
        BookSide bookSide = new BookSide(side);
        for (Order order : orders) {
            bookSide.postOrder(order);
        }
        BookSide.SidedQuote quote = bookSide.bestBidOffer();
        assertThat(quote).isEqualTo(expectedQuote);
    }

    public void testAttemptToFill() {
        BookSide bookSide = new BookSide(Side.BUY);
        bookSide.postOrder(limit(Side.BUY, 100, 100.25));

        // Exact price and qty
        List<Fill> fills = bookSide.attemptToFill(limit(Side.SELL, 100, 100.25));
        assertThat(fills).containsExactly(Fill.from(100.25, 100));
        assertThat(bookSide.bestBidOffer()).isEqualTo(BookSide.SidedQuote.from(90, 100.25));





    }
    public static Order limit(Side side,  long qty, double price) {
        return new Order(side, OrderType.LIMIT, qty, price);
    }
    public static Order market(Side side,  long qty) {
        return new Order(side, OrderType.MARKET, qty, Double.NaN);
    }
    public static BookSide.SidedQuote sidedQuote(double price, long qty) {
        return new BookSide.SidedQuote(Price.of(price), qty);
    }
}