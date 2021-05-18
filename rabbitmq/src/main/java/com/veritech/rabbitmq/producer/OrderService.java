package com.veritech.rabbitmq.producer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.veritech.rabbitmq.config.MessagesQueueingCommand;
import com.veritech.rabbitmq.dto.Order;
import com.veritech.rabbitmq.dto.OrderStatus;

@Service
public class OrderService {
	@Autowired
	@Qualifier("orderQueueCommanderImpl")
	private MessagesQueueingCommand<OrderStatus> template;
	
	public OrderStatus processOrder(Order order, String restaurantName) {
		int number = order.getNumber();
		OrderStatus orderStatus = null ;
		for(int i=0;i<number;i++) {
			order.setOrderId(UUID.randomUUID().toString());
			order.setDemandedRestaurantName(restaurantName);
			
			orderStatus = new OrderStatus();
			orderStatus.setOrder(order);
			orderStatus.setStatus("processing");
			orderStatus.setMessage("restaurant "+restaurantName+" is processing the order.");
			order.setNumber(i);
			template.sendMessage(orderStatus);
		}

		return orderStatus;
	}
}
