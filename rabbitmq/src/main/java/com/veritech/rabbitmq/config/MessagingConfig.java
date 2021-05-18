package com.veritech.rabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.veritech.rabbitmq.consumer.OrderConsumerService;

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
	public Queue queue() {
		Queue q =  new Queue(QUEUE_NAME	);
		q.addArgument(X_QUEUE_TYPE,X_QUEUE_TYPE_PROPERTY);
		q.addArgument(X_DEAD_LETTER_EXCHANGE,X_DEAD_LETTER_EXCHANGE_PROPERTY);
		q.addArgument(X_DEAD_LETTER_ROUTING_KEY, X_DEAD_LETTER_ROUTING_KEY_PROPERTY);
		return q;
	}
	
	@Bean
	public Queue queueDLX() {
		Queue q =  new Queue(DLX_QUEUE_NAME	);
		q.addArgument(X_QUEUE_TYPE,X_QUEUE_TYPE_PROPERTY);
		return q;
	}
	@Bean
	public Exchange exchangeDLX() {
		return new TopicExchange(X_DEAD_LETTER_EXCHANGE_PROPERTY);
	}
	
	@Bean
	public Exchange exchange() {
		return new TopicExchange(ORDER_EXCHANGE_NAME);
	}

	@Bean
	public Binding dlxBinding (@Autowired Queue queueDLX, @Autowired Exchange exchangeDLX) {
		return BindingBuilder.bind(queueDLX).to(exchangeDLX).with(X_DEAD_LETTER_ROUTING_KEY_PROPERTY).noargs();
	}
	@Bean
	public Binding binding (@Autowired Queue queue, @Autowired Exchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY).noargs();
	}
	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	@Qualifier("messageTemplateImp")
	public AmqpTemplate messageTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		rabbitTemplate.setExchange(ORDER_EXCHANGE_NAME);
//		rabbitTemplate.setRoutingKey(ORDER_ROUTING_KEY);
		rabbitTemplate.setMessageConverter(messageConverter);
		return rabbitTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(messageListenerAdapter);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		return container;
	}
	@Bean
	public MessageListenerAdapter listenerAdapter(OrderConsumerService receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
