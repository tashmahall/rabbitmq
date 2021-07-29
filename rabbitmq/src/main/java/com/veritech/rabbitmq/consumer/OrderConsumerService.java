package com.veritech.rabbitmq.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.veritech.rabbitmq.dto.OrderStatus;

import br.gov.ans.snirabbitmq.core.AckConfirmConsumer;
import br.gov.ans.snirabbitmq.core.MessageConverter;
import br.gov.ans.snirabbitmq.core.exceptions.SNIRabbitMQException;

public class OrderConsumerService implements DeliverCallback,AckConfirmConsumer {

	private static final Logger log = LoggerFactory.getLogger(OrderConsumerService.class); // NOSONAR
	private MessageConverter messageConverter;
	private Channel channel;
	@Override
	public void handle(String consumerTag, Delivery message) throws IOException {
		try {
			log.debug(consumerTag);
			OrderStatus status = messageConverter.fromMessage(message.getBody(), OrderStatus.class);
			if (status.getOrder().getNumber() % 5 == 0) { // mod 2
				log.debug("rejected to dlx message: " + status);
				return;
			}
			if (status.getOrder().getNumber() % 3 == 0) { // mod 3
				log.debug("no ack message " + status);
				return;
			}

			log.debug("ACK OK " + status); // resto
			return;

		} catch (IOException e) {
			throw new SNIRabbitMQException(e);
		}

	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)	throws IOException {
		try {
			log.debug(consumerTag);
			OrderStatus status = messageConverter.fromMessage(body, OrderStatus.class);
			if (status.getOrder().getNumber() % 5 == 0) { // mod 2
				log.debug("rejected to dlx message: " + status);
				getChannel().basicReject(envelope.getDeliveryTag(), false);
				return;
			}
			if (status.getOrder().getNumber() % 3 == 0) { // mod 3
				log.debug("no ack message " + status);
				getChannel().basicNack(envelope.getDeliveryTag(), false, true);
				return;
			}

			log.debug("ACK OK " + status); // resto
			getChannel().basicAck(envelope.getDeliveryTag(), false);
			return;

		} catch (IOException e) {
			throw new SNIRabbitMQException(e);
		}

	}
	@Override
	public void handleConsumeOk(String consumerTag) {}
	@Override
	public void handleCancelOk(String consumerTag) {}
	@Override
	public void handleCancel(String consumerTag) throws IOException {}
	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {}
		
	@Override
	public void handleRecoverOk(String consumerTag) {}

		
	@Override
	public void setChannel(Channel channel) {
		this.channel=channel;
		
	}
	@Override
	public Channel getChannel() {
		return this.channel;
	}
	@Override
	public MessageConverter getMessageConverter() {
		return this.messageConverter;
	}

}
//@RabbitListener(queues = "test_quorum_queue-dlx",ackMode = "AUTO")
//public void processFailedMessagesRequeue(Message failedMessage) {
//    System.out.println("DLX queue "+failedMessage);
//    failedMessage.getMessageProperties().getXDeathHeader().forEach(x->{
//    	x.entrySet().forEach(y->{
//    		System.out.println(y.getKey()+";"+y.getValue());
//    	});
//    });
////	rabbitTemplate.send("test_quorum_queue-dlx",  failedMessage.getMessageProperties().getReceivedRoutingKey(), failedMessage);
//}
