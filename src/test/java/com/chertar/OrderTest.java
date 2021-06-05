package com.chertar;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.*;

public class OrderTest extends TestCase {

    public void testGetters() {
        Order order = new Order(Side.BUY, OrderType.LIMIT,  10, 100.01);
        assertThat(order.side()).isEqualTo(Side.BUY);
        assertThat(order.type()).isEqualTo(OrderType.LIMIT);
        assertThat(order.limitPrice().doubleValue()).isCloseTo(100.01, within(0.001));
        assertThat(order.qty()).isEqualTo(10);
    }

    public void testProcessFill() {
        Order order = new Order(Side.BUY, OrderType.LIMIT,  10, 100.00);

        // First fill
        {
            Fill fill = Fill.from(99.00, 4);
            order.processFill(fill);
            assertThat(order.filledQty()).isEqualTo(4);
            assertThat(order.leavesQty()).isEqualTo(6);
            assertThat(order.avgPrice().doubleValue()).isCloseTo(99.00, within(0.001));
            assertThat(order.isFullyFilled()).isFalse();
        }

        // Second and last fill
        {
            Fill fill = Fill.from(98.00, 6);
            order.processFill(fill);
            assertThat(order.filledQty()).isEqualTo(10);
            assertThat(order.leavesQty()).isEqualTo(0);
            // expectedAvgPrice is derived from ((99.0 * 4) + (98.0 * 6)) / (4 + 6) = 98.4;
            double expectedAvgPrice = 98.4;
            System.out.println(expectedAvgPrice);
            assertThat(order.avgPrice().doubleValue()).isCloseTo(expectedAvgPrice, within(0.001));
            assertThat(order.isFullyFilled()).isTrue();
        }

        //Attempt to overfill
        {
            Fill fill = Fill.from(98.00, 1);
            assertThatExceptionOfType(MatchingEngineException.class).isThrownBy(()->order.processFill(fill))
                    .withMessageContaining("overfill");

        }
    }
}