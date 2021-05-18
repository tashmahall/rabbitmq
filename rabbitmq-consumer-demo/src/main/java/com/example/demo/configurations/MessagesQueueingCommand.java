package com.example.demo.configurations;
@FunctionalInterface
public interface MessagesQueueingCommand<E> {
	public void sendMessage(E entityMessage);
}
