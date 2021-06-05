package com.chertar;

import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;


public class BookSideTest extends TestCase {

    public void testPostingBuyOrders() {
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

    private static void testPermutation(Side side, List<Order> orders, BookSide.SidedQuote expectedQuote) {
        BookSide bookSide = new BookSide(side);
        for (Order order : orders) {
            bookSide.postOrder(order);
        }
        BookSide.SidedQuote quote = bookSide.bestBidOffer();
        assertThat(quote).isEqualTo(expectedQuote);
    }

    public void testAttemptToFill() {
    }
    public static Order limit(Side side,  long qty, double price) {
        return new Order(side, OrderType.LIMIT, qty, price);
    }
    public static BookSide.SidedQuote sidedQuote(double price, long qty) {
        return new BookSide.SidedQuote(Price.of(price), qty);
    }
}