package com.chertar;

import junit.framework.TestCase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;


public class BookSideTest extends TestCase {

    public void testPostOrder() {
        testPermutation(list(limit(Side.BUY, 100, 100.25)), sidedQuote(100.25, 100));
    }

    private static void testPermutation(List<Order> orders, BookSide.SidedQuote expectedQuote) {
        BookSide bookSide = new BookSide(Side.BUY);
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