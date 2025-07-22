package com.nhnacademy.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "user-exchange";
    public static final String QUEUE_NAME = "welcome-coupon-queue";
    public static final String ROUTING_KEY = "user.registered";

    public static final String BIRTHDAY_EXCHANGE_NAME = "birthday-exchange";
    public static final String BIRTHDAY_QUEUE_NAME = "birthday-queue";
    public static final String BIRTHDAY_ROUTING_KEY = "birthday.user";

    public static final String COUPON_ISSUING_STARTED_EXCHANGE = "coupon.issuing.started.exchange";
    public static final String COUPON_ISSUING_STARTED_ROUTING_KEY = "coupon.issuing.started.event";

    public static final String ISSUE_COUPONS_TO_USERS_EXCHANGE = "issue.coupons.to.users.exchange";
    public static final String ISSUE_COUPONS_TO_USERS_QUEUE = "issue.coupons.to.users.queue";
    public static final String ISSUE_COUPONS_TO_USERS_ROUTING_KEY = "issue.coupons.to.users.request";


    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue welcomeCouponQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(Queue welcomeCouponQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(welcomeCouponQueue).to(userExchange).with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange birthdayExchange() {
        return new DirectExchange(BIRTHDAY_EXCHANGE_NAME);
    }

    @Bean
    public Queue birthdayQueue() {
        return new Queue(BIRTHDAY_QUEUE_NAME, true);
    }

    @Bean
    public Binding birthdayBinding(Queue birthdayQueue, DirectExchange birthdayExchange) {
        return BindingBuilder.bind(birthdayQueue).to(birthdayExchange).with(BIRTHDAY_ROUTING_KEY);
    }


    @Bean
    public TopicExchange couponIssuingStartedExchange() {
        return new TopicExchange(COUPON_ISSUING_STARTED_EXCHANGE);
    }

    @Bean
    public DirectExchange issueCouponsToUsersExchange() {
        return new DirectExchange(ISSUE_COUPONS_TO_USERS_EXCHANGE);
    }

    @Bean
    public Queue issueCouponsToUsersQueue() {
        return new Queue(ISSUE_COUPONS_TO_USERS_QUEUE, true);
    }

    @Bean
    public Binding issueCouponsToUsersBinding(Queue issueCouponsToUsersQueue, DirectExchange issueCouponsToUsersExchange) {
        return BindingBuilder.bind(issueCouponsToUsersQueue).to(issueCouponsToUsersExchange).with(ISSUE_COUPONS_TO_USERS_ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
