package com.example.demo.configurations;


import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.example.demo.dtos.OrderStatus;

import static com.example.demo.configurations.MessagingConfig.ORDER_EXCHANGE_NAME;
import static com.example.demo.configurations.MessagingConfig.ORDER_ROUTING_KEY;
import static com.example.demo.configurations.MessagingConfig.QUEUE_NAME;

@Component
@Qualifier("orderQueueCommanderImpl")
public class OrderQueueCommanderImpl implements MessagesQueueingCommand<OrderStatus>{
	@Autowired
	@Qualifier("messageTemplateImp")
	private AmqpTemplate template;
	@Override
	public void sendMessage(OrderStatus message) {
		template.convertAndSend(ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY, message);
		
	}

}
