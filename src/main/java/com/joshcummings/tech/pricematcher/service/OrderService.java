package com.joshcummings.tech.pricematcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joshcummings.tech.pricematcher.model.Book;
import com.joshcummings.tech.pricematcher.model.Order;
import com.joshcummings.tech.pricematcher.model.Orders;

/**
 * A service for placing stock orders.
 * 
 * @author jzheaux
 *
 */
public class OrderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
	
	private final Orders buys = new Orders(Orders.DESCENDING);
	private final Orders sells = new Orders(Orders.ASCENDING);
	
	public Book getBook() {
		return new Book(buys, sells);
	}
	
	/**
	 * Place a stock order to purchase a given {@code quantity} and maximum {@code price}.
	 * 
	 * NOTE: This is NOT thread-safe, though a marginal amount of effort has been made so that
	 * making it thread safe is a bit easier.
	 * 
	 * The specific problem is that the {@link OrderService#sell(Order)} method can add elements
	 * to the same list that this method is reading against. Copies, introducing a buffer, or 
	 * introducing a mutex would be potential ways to make the method thread-safe.
	 * 
	 * @param buy
	 */
	public void buy(Order buy) {
		LOGGER.info("Received buy order: {}", buy);
		
		// depending on the success of the match, the buy order could be completely exhausted,
		// so the offer method checks to see if the quantity is null before adding to the queue
		buys.offer(sells.match(buy));
	}
	
	/**
	 * Place a stock order to sell a given {@code quantity} and minimum {@code price}.
	 * 
	 * NOTE: This is NOT thread-safe, though a marginal amount of effort has been made so that
	 * making it thread safe is a bit easier.
	 * 
	 * The specific problem is that the {@link OrderService#buy(Order)} method can add elements
	 * to the same list that this method is reading against. Copies, introducing a buffer, or 
	 * introducing a mutex would be potential ways to make the method thread-safe.
	 * 
	 * @param buy
	 */
	public void sell(Order sell) {
		LOGGER.info("Received sell order: {}", sell);
		sells.offer(buys.match(sell));
	}
}
