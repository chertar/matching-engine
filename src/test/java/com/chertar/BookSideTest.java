package com.chertar;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;


public class BookSideTest extends TestCase {

    public void testPostOrder() {
        BookSide bookSide = new BookSide(Side.BUY);
        Order order = limit(Side.BUY, 100, "BTC-USD",100.25 );
        bookSide.postOrder(order);
        BookSide.SidedQuote quote = bookSide.bestBidOffer();
        assertThat(quote).isEqualTo(halfQuote(100.25, 100));
    }

    public void testAttemptToFill() {
    }
    public Order limit(Side side,  long qty, String instrument, double price) {
        return new Order(side, OrderType.LIMIT, qty, price);
    }
    public BookSide.SidedQuote halfQuote(double price, long qty) {
        return new BookSide.SidedQuote(Price.of(price), qty);
    }
}