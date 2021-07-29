package com.veritech.rabbitmq.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.veritech.rabbitmq.consumer.OrderConsumerService;

import br.gov.ans.snirabbitmq.config.SNIRabbitMQConnectionFactory;
import br.gov.ans.snirabbitmq.core.AckConfirmConsumer;
import br.gov.ans.snirabbitmq.core.AcknowledgeMode;
import br.gov.ans.snirabbitmq.core.MessageConverter;
import br.gov.ans.snirabbitmq.core.QueueConsumerManager;
import br.gov.ans.snirabbitmq.core.SNIMessageJSONConverter;
import br.gov.ans.snirabbitmq.core.SNIRabbitMQTemplate;
import br.gov.ans.snirabbitmq.core.SNNISimpleQueueConsumer;

@Configuration
public class MessagingConfig {
	public static final String QUEUE_NAME = "test_quorum_queue2";
	public static final String ORDER_EXCHANGE_NAME = "exchage_quorum_test";
	public static final String ORDER_ROUTING_KEY = "quorum.routing2";
	

	public static final String X_QUEUE_TYPE = "x-queue-type";
	public static final String X_QUEUE_TYPE_PROPERTY = "quorum";
	
	public static final String DLX_QUEUE_NAME = "test_quorum_queue-dlx";
	public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
	public static final String X_DEAD_LETTER_EXCHANGE_PROPERTY = "test_quorum_queue-dlx";
//	public static final String X_DEAD_LETTER_EXCHANGE_PROPERTY = "";
	
	public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
	public static final String X_DEAD_LETTER_ROUTING_KEY_PROPERTY = "dlx.quorum.routing2";

	@Bean
	@Qualifier("SNIMessageJSONConverter")
	public MessageConverter messageConverter() {
		return new SNIMessageJSONConverter();
	}

	@Bean
	@Qualifier("messageTemplateImp")
	public SNIRabbitMQTemplate messageTemplate(SNIRabbitMQConnectionFactory connectionFactory, @Autowired @Qualifier("SNIMessageJSONConverter") MessageConverter messageConverter) {
		SNIRabbitMQTemplate rabbitTemplate = new SNIRabbitMQTemplate(connectionFactory);
//		rabbitTemplate.setExchange(ORDER_EXCHANGE_NAME);
//		rabbitTemplate.setRoutingKey(ORDER_ROUTING_KEY);
		rabbitTemplate.setMessageConverter(messageConverter);
		return rabbitTemplate;
	}

	@Bean
	public SNNISimpleQueueConsumer container(QueueConsumerManager consumerManager, SNIRabbitMQConnectionFactory connectionFactory, @Qualifier("OrderConsumerService") AckConfirmConsumer messageReader) {
		SNNISimpleQueueConsumer queueConsumer =  new SNNISimpleQueueConsumer(connectionFactory,messageReader,QUEUE_NAME,1,AcknowledgeMode.MANUAL);
		consumerManager.addQueueConsumer(queueConsumer);
		return queueConsumer;
	}
	@Bean
	@Qualifier("OrderConsumerService")
	public AckConfirmConsumer listenerAdapter( MessageConverter messageConverter,SNIRabbitMQConnectionFactory connectionFactory) {
		OrderConsumerService consumer = new OrderConsumerService();
		consumer.setMessageConverter(messageConverter);
		return consumer;

	}
}
