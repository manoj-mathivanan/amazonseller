package com.example;

public class Order {
	String order_id;
	String asin;
	int quantity;
	String date;
	String status;
	String sku;
	public Order(String order_id,String asin,String date, String status,String sku, int quantity) {
		this.order_id = order_id;
		this.asin = asin;
		this.date = date;
		this.status = status;
		this.sku = sku;
		this.quantity = quantity;
	}
}
