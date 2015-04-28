package com.joshcummings.tech.pricematcher.service;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.joshcummings.tech.pricematcher.model.Book;
import com.joshcummings.tech.pricematcher.model.Order;

public class OrderServiceTest {

	@Test
	public void testBuyWithNoSellers() {
		OrderService os = new OrderService();
		Order buy = new Order(2L, new BigDecimal(10));
		os.buy(buy);
		Book b = os.getBook();
		Assert.assertEquals(1, b.getBuys().size());
		Assert.assertEquals(0, b.getSells().size());
	}

	@Test
	public void testSellWithNoBuyers() {
		OrderService os = new OrderService();
		Order sell = new Order(1L, new BigDecimal(15));
		os.sell(sell);
		Book b = os.getBook();
		Assert.assertEquals(0, b.getBuys().size());
		Assert.assertEquals(1, b.getSells().size());
	}
	
	@Test
	public void testBuyWithOneCompatibleSeller() {
		OrderService os = new OrderService();
		Order sell = new Order(1L, new BigDecimal(15));
		os.sell(sell);
		sell = new Order(1L, new BigDecimal(20));
		os.sell(sell);
		Order buy = new Order(1L, new BigDecimal(18));
		os.buy(buy);
		Book b = os.getBook();
		Assert.assertEquals(0, b.getBuys().size());
		Assert.assertEquals(1, b.getSells().size());
	}
	
	@Test
	public void testSellWithOneCompatibleBuyer() {
		OrderService os = new OrderService();
		Order buy = new Order(1L, new BigDecimal(16));
		os.buy(buy);
		buy = new Order(1L, new BigDecimal(13));
		os.buy(buy);
		Order sell = new Order(1L, new BigDecimal(15));
		os.sell(sell);
		Book b = os.getBook();
		Assert.assertEquals(1, b.getBuys().size());
		Assert.assertEquals(0, b.getSells().size());
	}
	
	@Test
	public void testSellWithTwoCompatibleBuyers() {
		OrderService os = new OrderService();
		Order buy = new Order(3L, new BigDecimal(12));
		os.buy(buy);
		buy = new Order(2L, new BigDecimal(13));
		os.buy(buy);
		Order sell = new Order(4L, new BigDecimal(11));
		os.sell(sell);
		Book b = os.getBook();
		Assert.assertEquals(1, b.getBuys().size());
		Assert.assertEquals(0, b.getSells().size());
		Order o = b.getBuys().iterator().next();
		Assert.assertEquals(new Long(1L), o.getQuantity());
		Assert.assertEquals(new BigDecimal(12), o.getPrice());
	}
	
	@Test
	public void testTrentsData() {
		OrderService os = new OrderService();
		Order buy = new Order(2L, new BigDecimal(10));
		os.buy(buy);
		buy = new Order(2L, new BigDecimal(11));
		os.buy(buy);
		buy = new Order(2L, new BigDecimal(9));
		os.buy(buy);
		Order sell = new Order(1L, new BigDecimal(15));
		os.sell(sell);
		sell = new Order(1L, new BigDecimal(14));
		os.sell(sell);
		sell = new Order(1L, new BigDecimal(16));
		os.sell(sell);
		sell = new Order(3L, new BigDecimal(10));
		os.sell(sell);
		buy = new Order(3L, new BigDecimal(14));
		os.buy(buy);
		buy = new Order(2L, new BigDecimal(16));
		os.buy(buy);
		sell = new Order(5L, new BigDecimal(9));
		os.sell(sell);
		Book b = os.getBook();
		Assert.assertEquals(0, b.getBuys().size());
		Assert.assertEquals(0, b.getSells().size());
	}
}
