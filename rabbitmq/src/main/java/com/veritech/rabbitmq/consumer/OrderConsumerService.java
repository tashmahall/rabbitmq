package com.veritech.rabbitmq.consumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.veritech.rabbitmq.dto.OrderStatus;
@Component
public class OrderConsumerService implements ChannelAwareMessageListener {
	@Autowired	@Qualifier("messageTemplateImp")
	private AmqpTemplate rabbitTemplate;
	public void consumeQueueTest(OrderStatus startus) {
		System.out.println(startus);
	}
	@Override
	public void onMessage(Message message,Channel channel) throws IOException, InterruptedException, TimeoutException  {
		ObjectMapper om = new ObjectMapper();
		try {
			int number = 8;
			OrderStatus status = om.readValue(message.getBody(), OrderStatus.class);
			Thread.sleep(1000);
				if (status.getOrder().getNumber() >= number){
					System.out.println("rejected to dlx: "+status);
					message.getMessageProperties().setHeader("x-business-error",	"unrecoverable-error");
//					message.getMessageProperties().setHeader("x-delivery-count", 5);
					channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);

//					throw new OrderException("rejected to dlx: "+status);
//					channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//					channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);

				}
				if (status.getOrder().getNumber() == number-1) {
					System.out.println("no ack "+status);
				}
				if (status.getOrder().getNumber() < number-1) {
					channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
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
	@RabbitListener(queues = "test_quorum_queue-dlx",ackMode = "AUTO")
	public void processFailedMessagesRequeue(Message failedMessage) {
	    System.out.println("DLX queue "+failedMessage);
	    failedMessage.getMessageProperties().getXDeathHeader().forEach(x->{
	    	x.entrySet().forEach(y->{
	    		System.out.println(y.getKey()+";"+y.getValue());
	    	});
	    });
//		rabbitTemplate.send("test_quorum_queue-dlx",  failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage);
	}
}
