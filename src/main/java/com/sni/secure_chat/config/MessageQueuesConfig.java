package com.sni.secure_chat.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageQueuesConfig {
    @Value("${queues.number}")
    private Integer numberOfQueues;

    @Bean
    Queue queue1() {
        return new Queue("1", false);
    }

    @Bean
    Queue queue2() {
        return new Queue("2", false);
    }

    @Bean
    Queue queue3() {
        return new Queue("3", false);
    }

    @Bean
    Queue queue4() {
        return new Queue("4", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("exchange");
    }

    @Bean
    Binding binding1(@Qualifier("queue1") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("1");
    }

    @Bean
    Binding binding2(@Qualifier("queue2") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("2");
    }

    @Bean
    Binding binding3(@Qualifier("queue3") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("3");
    }

    @Bean
    Binding binding4(@Qualifier("queue4") Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("4");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
