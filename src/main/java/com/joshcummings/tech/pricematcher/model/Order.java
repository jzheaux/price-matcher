package com.joshcummings.tech.pricematcher.model;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

/**
 * A domain object that represents a request to buy or sell a certain {@code quantity} of stock at or above/below
 * a certain {@code price}
 * 
 * @author jzheaux
 *
 */
public class Order {
	/*
	 * I'm not a big fan of using third-party references in my domain objects, but
	 * it was quick and hopefully made the solution a little lighter than going with the
	 * Java standard.
	 * 
	 * Note that the values are immutable. Generally, I think this is better practice.
	 */
	@SerializedName("qty")
	private final Long quantity;
	
	/*
	 * This is a BigDecimal since I would suppose that prices can get much finer than two decimal places
	 */
	@SerializedName("prc")
	private final BigDecimal price;
	
	/**
	 * 
	 * @param quantity - How many of the stock to purchase
	 * 
	 * @param price - For a sell, this is the minimum price the seller wishes to sell the stock for. For a buy,
	 * this is the maximum price the buyer wishes to buy the stock for
	 */
	public Order(Long quantity, BigDecimal price) {
		this.quantity = quantity;
		this.price = price;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public Long getQuantity() {
		return quantity;
	}
	
	/**
	 * For debugging purposes
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Quantity = [%s]; Price = [%s]", quantity, price);
	}
}
