package com.chertar;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PriceLevelTest extends TestCase {

    public void testPrice() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        assertThat(level.price()).isEqualTo(Price.of(100.25));
    }

    public void testPostOrder() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        assertThat(level.queueSize()).isEqualTo(0);

        // Add one order and verify queue size
        Order order1 = new Order(Side.BUY, OrderType.LIMIT, 100.25, 10);
        level.postOrder(order1);
        assertThat(level.queueSize()).isEqualTo(1);

        // Add second order and verify queue size
        Order order2 = new Order(Side.BUY, OrderType.LIMIT, 100.25, 20);
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
        Order order = new Order(Side.BUY, OrderType.LIMIT, 101.00, 10);
        assertThatExceptionOfType(MatchingEngineException.class)
                .isThrownBy(() -> level.postOrder(order))
                .withMessageContaining("Order and level prices don't match");
    }
    public void testMarketOrder() {
        PriceLevel level = new PriceLevel(Price.of(100.25));
        Order order = new Order(Side.BUY, OrderType.MARKET, Double.NaN, 10);
        assertThatExceptionOfType(MatchingEngineException.class)
                .isThrownBy(()-> level.postOrder(order))
                .withMessageContaining("Market order cannot be posted");
    }

}