package com.veritech.rabbitmq.consumer;

public class OrderException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderException(String string) {
		super(string);
	}

}
