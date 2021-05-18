package com.veritech.rabbitmq.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.veritech.rabbitmq.dto.OrderStatus;

import static com.veritech.rabbitmq.config.MessagingConfig.ORDER_EXCHANGE_NAME;
import static com.veritech.rabbitmq.config.MessagingConfig.ORDER_ROUTING_KEY;;

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
