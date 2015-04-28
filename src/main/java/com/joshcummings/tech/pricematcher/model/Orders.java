package com.joshcummings.tech.pricematcher.model;

import java.math.BigDecimal;
import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some of this code, while still my own original work, is duplicative. Java doesn't make
 * the internals of LinkedList available to me (like the head and tail pointers); otherwise,
 * a fair amount of it would disappear.
 * 
 * Still, the heart of the implementation is unique, so I don't regret creating it. I believe
 * there are nice semantics around queues that make for clean matching code.
 * 
 * Most importantly, I believe that using a Queue here is specifically valuable because
 * the match method is constantly plucking the front off while offer is adding to the back. This means 
 * that the match method can operate in O(n) time with an average performance that is much better due to
 * it dropping off as soon as the order is exhausted. Using a PriorityQueue would give this algorithm
 * a classification of O(n log n)
 * 
 * That said, empirical data would need to be gathered to determine whether match is being called
 * more often or offer since, due to this implementation, offer also has a worst case of O(n).
 * 
 * If sells and buys are about equal, then match and offer are called the same number of times, which
 * gives an estimated classification of = match (O(n)) + offer (O(n)) = O(n). However, with PriorityQueue,
 * match' (O(n log n)) + offer' (O(log n)) = O(n log n).
 * 
 * Lastly, this doesn't extend from LinkedList since we can't effectively override any of the important
 * methods (since we don't have access to head and tail); though semantically clearer, I didn't see much point.
 * 
 * @author jzheaux
 *
 */
public class Orders extends AbstractQueue<Order> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Orders.class);
	
	public static final Comparator<Order> ASCENDING = (o1, o2) -> o1.getPrice().compareTo(o2.getPrice());
	public static final Comparator<Order> DESCENDING = (o1, o2) -> o2.getPrice().compareTo(o1.getPrice());

	private Node<Order> head;
	private Node<Order> tail;
	private Comparator<Order> comparator;
	private int size;
	
	/**
	 * 
	 * @param comparator - To use when {@link Orders#offer(Order)}-ing an order into the queue,
	 * also used to compare a sell order to a buy order and vice-versa in {@link Orders#match(Order)}
	 */
	public Orders(Comparator<Order> comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Order poll() {
		if ( head == null ) {
			return null;
		}

		Order toReturn = head.data;
		head = head.next;
		
		if ( head != null ) {
			head.previous = null;
		}
		
		size--;
		
		return toReturn;
	}
	
	/**
	 * Adds to the end of the queue and then promotes it through the queue in accordance with 
	 * the supplied {@code comparator} in the constructor.
	 */
	@Override
	public boolean offer(Order o) {
		if ( o.getQuantity() <= 0 ) {
			return false;
		}
		
		size++;
		
		if ( tail == null ) {
			tail = new Node<>(o);
			head = tail;
			return true;
		}
		
		if ( comparator.compare(tail.data, o) < 0 ) {
			Node<Order> temp = tail;
			tail = new Node<>(o);
			tail.previous = temp;
			temp.next = tail;
			return true;
		}
		
		Node<Order> current = tail;
		while ( current.previous != null && comparator.compare(current.previous.data, o) > 0 ) {
			current = current.previous;
		}
		
		Node<Order> temp = current.previous;
		Node<Order> n = new Node<>(o);
		current.previous = n;
		n.previous = temp;
		n.next = current;
		if ( temp == null ) {
			head = n;
		} else {
			temp.next = n;
		}

		return true;
	}
	
	/**
	 * A helper method to place an order at the front regardless of priority.
	 * 
	 * Should only be used when updating head's data in such a way as to not affect the comparator's decision
	 * 
	 * NOTE: A more cohesive way to introduce this method would be to have something similar to offer, 
	 * which puts the order at the back and then promotes. This should put the order at the front and then
	 * demote. Currently, it is coincidence that we are always only pulling off the head, changing portions of
	 * data that don't affect the order, and then placing it back on. Leaving the method as is
	 * could be a hindrance to thread-safety unless we want to completely block off access to the queue to more
	 * than one thread at a time.
	 * 
	 * @param order
	 */
	private boolean offerFirst(Order order) {
		if ( head == null ) {
			return offer(order);
		} else {
			Node<Order> temp = head;
			head = new Node<>(order);
			head.next = temp;
			temp.previous = head;
			size++;
			return true;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Order peek() {
		if ( head == null ) {
			return null;
		}
		
		return head.data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Order> iterator() {
		return new Iterator<Order>() {
			private Node<Order> current = head;
			
			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public Order next() {
				if ( current == null ) {
					throw new NoSuchElementException("No such element!");
				}
				Order toReturn = current.data;
				current = current.next;
				return toReturn;
			}
		};
	}
	
	/**
	 * Find any orders that match in the queue according to the order's criteria. In the case of a purchase,
	 * the order will remove as many of each sell order in order of ascending sell price as possible, starting 
	 * with the lowest priced. In the case of a sell, the order will remove as many of each buy order in order of
	 * descending purchase price as possible, starting with the highest priced.
	 * 
	 * This method leverages the comparable passed into the constructor. This is an optimization that may not
	 * stand the test of time. Down the road, other circumstances may dictate that the way that the orders are ordered
	 * in the queue and the way orders are given priority may diverge. For now, it seems like a good bargain, though.
	 * 
	 * NOTE: This method makes the assumption that one cannot order a negative quantity nor specify a negative price
	 * and throws an {@link IllegalArgumentException} accordingly.
	 * 
	 * @param order
	 * @return
	 * @throws {@link IllegalArgumentException} - see above
	 */
	public Order match(Order order) {
		if ( order.getQuantity() <= 0 || order.getPrice().compareTo(BigDecimal.ZERO) < 0 ) {
			throw new IllegalArgumentException("Quantity must be positive, price must be non-negative");
		}
		
		Order toMatch = order;
		
		while ( head != null && toMatch.getQuantity() > 0 && comparator.compare(toMatch, head.data) >= 0 ) {
			Order opposite = poll();
			LOGGER.debug("Found a match: [{}] -> [{}]", toMatch, opposite);
			
			if ( toMatch.getQuantity() < opposite.getQuantity() ) {
				LOGGER.debug("Will satisfy remaining quantity [{}] with [{}]", toMatch.getQuantity(), opposite);
				
				offerFirst(new Order(opposite.getQuantity() - toMatch.getQuantity(), opposite.getPrice()));
				toMatch = new Order(0L, toMatch.getPrice());
			} else {
				LOGGER.debug("Will satisfy part of the quantity [{}] with [{}]", opposite.getQuantity(), opposite);
				
				toMatch = new Order(toMatch.getQuantity() - opposite.getQuantity(), toMatch.getPrice());
			}
		}
		
		return toMatch;
	}	

	/**
	 * A simple Node class, typical of Linked Lists
	 * @author jzheaux
	 *
	 * @param <T>
	 */
	private class Node<T> {
		T data;
		Node<T> next;
		Node<T> previous;
		
		public Node(T o) {
			this.data = o;
		}
		
		@Override
		public String toString() {
			return data.toString();
		}
	}
}
