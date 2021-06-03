package com.chertar;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class OrderTest extends TestCase {

    public void testGetters() {
        Order order = new Order(Side.BUY, OrderType.LIMIT, 100.01, 10);
        assertThat(order.side()).isEqualTo(Side.BUY);
        assertThat(order.type()).isEqualTo(OrderType.LIMIT);
        assertThat(order.limitPrice().doubleValue()).isCloseTo(100.01, within(0.001));
        assertThat(order.qty()).isEqualTo(10);
    }

    public void testProcessFill() {
        Order order = new Order(Side.BUY, OrderType.LIMIT, 100.00, 10);

        // First fill
        Fill fill = Fill.from(101.00, 4);
        order.processFill(fill);
        assertThat(order.filledQty()).isEqualTo(4);
        assertThat(order.leavesQty()).isEqualTo(6);
        assertThat(order.avgPrice().doubleValue()).isCloseTo(101.00, within(0.001));

        // Second and last fill
    }

    public void testIsFullyFilled() {
    }

    public void testLeavesQty() {
    }
}