package com.bestpay.insurance.cbs.common.config;

import java.io.UnsupportedEncodingException;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bestpay.insurance.cbs.common.utils.Base64Utils;

import lombok.extern.slf4j.Slf4j;



@Configuration
@Slf4j
public class RabbitMQConfig {
	// 服务地址
	@Value("${rabbitmq.host}")
	private String host;
	// 服务端口
	@Value("${rabbitmq.port}")
	private String port;
	// 用户
	@Value("${rabbitmq.username}")
	private String rabbitmqUsername;
	// 密码
	@Value("${rabbitmq.password}")
	private String rabbitmqPassword;
	// 虚拟端口
	@Value("${rabbitmq.virtual}")
	private String virtual;
	// 队列名
	@Value("${rabbitmq.queueName}")
	private String queueName;
	// 交换器
	@Value("${rabbitmq.exchangeName}")
	private String exchangeName;
	// 路由规则
	@Value("${rabbitmq.routingKey}")
	private String routingKey;

	// 提供缓存JMS资源功能
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		// 解密rabbitMQ用户,密码
		String username = null;
		String password = null;
		try {
			username = Base64Utils.decoderStr(rabbitmqUsername);
			log.info("cbs的base64解密rabbit用户字段成功，解密前值为：{}，解密后的值为：{}", rabbitmqUsername, username);
		} catch (UnsupportedEncodingException e) {
			log.error("cbs的base64解密rabbit用户字段失败，请检查：{}", e.toString());
		}
		try {
			password = Base64Utils.decoderStr(rabbitmqPassword);
			log.info("cbs的base64解密rabbit用户密码字段成功，解密前值为：{}，解密后的值为：{}", rabbitmqPassword, password);
		} catch (UnsupportedEncodingException e) {
			log.error("cbs的base64解密rabbit用户密码字段失败，请检查：{}", e.toString());
		}
		// 设置地址 = 服务地址+服务端口
		connectionFactory.setAddresses(host + ":" + port);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(virtual);
		connectionFactory.setPublisherConfirms(true);// 设置提交
		return connectionFactory;
	}

	// 如果要进行发送消息 需要得到模板RabbitTemplate,监听程序可不需要
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory ) {
	    RabbitTemplate template = new RabbitTemplate(connectionFactory);
	    return template;
	}
	//DirectExchange
	@Bean
	public DirectExchange defaultExchange() {
	    return new DirectExchange(exchangeName);
	}
	//Queue，构建队列，名称，是否持久化之类
	@Bean
	public Queue queue() {
	    return new Queue(queueName, true);
	}
	//Binding，将DirectExchange与Queue进行绑定
	@Bean
	public Binding binding(DirectExchange defaultExchange) {
	    return BindingBuilder.bind(queue()).to(defaultExchange).with(routingKey);
	}
	//监听程序
	/*@Bean
	public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
	    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
	    container.setQueues(queue());
	    container.setExposeListenerChannel(true);
	    container.setMaxConcurrentConsumers(1);
	    container.setConcurrentConsumers(1);
	    container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
	    container.setMessageListener(new ChannelAwareMessageListener() {
	        public void onMessage(Message message, com.rabbitmq.client.Channel channel) throws Exception {
	            byte[] body = message.getBody();
	            log.info("监听消息：{}",body);
	            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	        }
	    });
	    return container;
	}*/


}
