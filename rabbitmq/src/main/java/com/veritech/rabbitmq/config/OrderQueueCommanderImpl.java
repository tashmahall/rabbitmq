package com.veritech.rabbitmq.config;

import static com.veritech.rabbitmq.config.MessagingConfig.ORDER_EXCHANGE_NAME;
import static com.veritech.rabbitmq.config.MessagingConfig.ORDER_ROUTING_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.veritech.rabbitmq.dto.OrderStatus;

import br.gov.ans.snirabbitmq.core.SNIRabbitMQTemplate;;

@Component
@Qualifier("orderQueueCommanderImpl")
public class OrderQueueCommanderImpl implements MessagesQueueingCommand<OrderStatus>{
	@Autowired
	@Qualifier("messageTemplateImp")
	private SNIRabbitMQTemplate template;
	@Override
	public void sendMessage(OrderStatus message) {
		template.convertAndSend(message, ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY, false);
		
	}

}
