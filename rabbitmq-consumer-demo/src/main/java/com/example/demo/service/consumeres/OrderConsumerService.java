package com.example.demo.service.consumeres;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

@Service
public class OrderConsumerService implements ChannelAwareMessageListener {
	

	public void consumeQueueTest(OrderStatus startus) {
		System.out.println(startus);
	}
	@Override
	public void onMessage(Message message,Channel channel) throws IOException, InterruptedException, TimeoutException  {
		ObjectMapper om = new ObjectMapper();
		try {
			
			OrderStatus status = om.readValue(message.getBody(), OrderStatus.class);
			Thread.sleep(250);
				if (status.getOrder().getNumber() == 99){
					System.out.println("rejected to dlx: "+status);
					message.getMessageProperties().setHeader("x-business-error",	"unrecoverable-error");
					channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
//					throw new OrderException("rejected to dlx: "+status);
//					channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//					channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

				}
				if (status.getOrder().getNumber() == 98) {
					System.out.println("no ack "+status);
				}
				if (status.getOrder().getNumber() != 98 && status.getOrder().getNumber() != 99) {
					channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
					System.out.println("ACK OK "+status);
				}

			
		} catch (IOException e) {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			channel.close();
		}
		
		
	}
}
