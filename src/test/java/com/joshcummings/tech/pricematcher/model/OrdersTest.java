package com.joshcummings.tech.pricematcher.model;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class OrdersTest {

	@Test
	public void testOffer() {
		Orders orders = new Orders(Orders.ASCENDING);
		orders.offer(new Order(2L, new BigDecimal(15)));
		orders.offer(new Order(2L, new BigDecimal(16)));
		orders.offer(new Order(2L, new BigDecimal(17)));
		Assert.assertEquals(3, orders.size());
		Assert.assertEquals(new BigDecimal(15), orders.peek().getPrice());
	}

	@Test
	public void testPoll() {
		Orders orders = new Orders(Orders.DESCENDING);
		orders.offer(new Order(2L, new BigDecimal(19)));
		orders.offer(new Order(2L, new BigDecimal(16)));
		orders.offer(new Order(2L, new BigDecimal(15)));
		orders.offer(new Order(2L, new BigDecimal(18)));
		orders.offer(new Order(2L, new BigDecimal(17)));
		orders.offer(new Order(2L, new BigDecimal(20)));
		Assert.assertEquals(new BigDecimal(20), orders.poll().getPrice());
		Assert.assertEquals(new BigDecimal(19), orders.poll().getPrice());
		Assert.assertEquals(new BigDecimal(18), orders.poll().getPrice());
	}
}
