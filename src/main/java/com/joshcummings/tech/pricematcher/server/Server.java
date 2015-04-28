package com.joshcummings.tech.pricematcher.server;

import spark.Spark;

import com.google.gson.Gson;
import com.joshcummings.tech.pricematcher.model.Order;
import com.joshcummings.tech.pricematcher.service.OrderService;

/**
 * Instead of using JSP/Servlets, I went for a lighter-weight solution in Spark Java.
 * 
 * @author jzheaux
 *
 */
public class Server {
	private static final String APPLICATION_JSON = "application/json";
	
	private static OrderService orderService = new OrderService();
	
	private static Gson gson = new Gson();
	
	private Server() {
		// thwart instantiation
	}
	
	private static String toJson(Object response) {
		return gson.toJson(response);
	}
	
	public static void main(String[] args) {
		Spark.port(3000);
		
		Spark.get("/book", APPLICATION_JSON,
				(request, response) -> orderService.getBook(),
				Server::toJson);
		
		Spark.post("/buy", APPLICATION_JSON,
				(request, response) -> {
					Order buy = gson.fromJson(request.body(), Order.class);
					orderService.buy(buy);
					return "OK";
				},
				Server::toJson);
		
		Spark.post("/sell", APPLICATION_JSON,
				(request, response) -> {
					Order sell = gson.fromJson(request.body(), Order.class);
					orderService.sell(sell);
					return "OK";
				},
				Server::toJson);
	}
}
