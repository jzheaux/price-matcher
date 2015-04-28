package com.joshcummings.tech.pricematcher.model;

import java.util.Collection;
import java.util.Collections;

/**
 * A read-only snapshot of the current book of unexhausted buys and sells
 * 
 * @author jzheaux
 *
 */
public class Book {
	private final Orders buys;
	private final Orders sells;
	
	/**
	 * 
	 * @param buys - The current unexhausted buys
	 * @param sells - The current unexhasted sells
	 */
	public Book(Orders buys, Orders sells) {
		this.buys = buys;
		this.sells = sells;
	}
	
	public Collection<Order> getBuys() {
		return Collections.unmodifiableCollection(buys);
	}
	
	public Collection<Order> getSells() {
		return Collections.unmodifiableCollection(sells);
	}
}
