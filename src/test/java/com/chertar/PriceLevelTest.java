package com.chertar;

import com.chertar.util.*;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PriceLevelTest extends TestCase {
    private Instrument instrument = Instrument.of("BTC-USD");
    private OrderIdGenerator idGenerator = new OrderIdGenerator();
    public void testPrice() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        assertThat(level.price()).isEqualTo(Price.of(100.25));
    }

    public void testPostOrder() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        assertThat(level.queueSize()).isEqualTo(0);

        // Add one order and verify queue size
        Order order1 = new Order(idGenerator.next(), instrument, Side.BUY, OrderType.LIMIT, 10,100.25);
        level.postOrder(order1);
        assertThat(level.queueSize()).isEqualTo(1);

        // Add second order and verify queue size
        Order order2 = new Order(idGenerator.next(), instrument, Side.BUY, OrderType.LIMIT, 20, 100.25 );
        level.postOrder(order2);
        assertThat(level.queueSize()).isEqualTo(2);

        // Verify peek returns first order
        assertThat(level.peek()).isEqualTo(order1);

        // Verify poll returns first orders and shrinks queue size
        assertThat(level.poll()).isEqualTo(order1);
        assertThat(level.queueSize()).isEqualTo(1);
        assertThat(level.peek()).isEqualTo(order2);
    }

    public void testInvalidOrderPrice() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        Order order = new Order(idGenerator.next(), instrument, Side.BUY, OrderType.LIMIT,  10, 101.00);
        assertThatExceptionOfType(MatchingEngineException.class)
                .isThrownBy(() -> level.postOrder(order))
                .withMessageContaining("Order and level prices don't match");
    }
    public void testMarketOrder() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        Order order = new Order(idGenerator.next(), instrument, Side.BUY, OrderType.MARKET,  10, Double.NaN);
        assertThatExceptionOfType(MatchingEngineException.class)
                .isThrownBy(()-> level.postOrder(order))
                .withMessageContaining("Market order cannot be posted");
    }

    private Order order(long qty) {
        return new Order(idGenerator.next(), instrument, Side.BUY, OrderType.LIMIT, qty, 100.25);
    }

    public void testCancelingOrder() {
        // Post three orders and cancel the middle one
        PriceLevel level = new PriceLevel(Price.of(100.25));

        Order order1 = order(10);
        Order order2 = order(20);
        Order order3 = order(30);

        level.postOrder(order1);
        level.postOrder(order2);
        level.postOrder(order3);

        assertThat(level.qty()).isEqualTo(60);

        level.cancel(order2);
        assertThat(order2.cancelled()).isTrue();
        assertThat(level.qty()).isEqualTo(40);

        // Make sure order2 is not returned when polling the queue
        assertThat(level.poll()).isEqualTo(order1);
        assertThat(level.poll()).isEqualTo(order3);
    }

}