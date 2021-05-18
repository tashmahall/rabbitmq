package com.veritech.rabbitmq.config;
@FunctionalInterface
public interface MessagesQueueingCommand<E> {
	public void sendMessage(E entityMessage);
}
