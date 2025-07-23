package com.nhnacademy.config;

import com.nhnacademy.common.config.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RabbitMQConfigTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange userExchange;

    @Autowired
    private Queue welcomeCouponQueue;

    @Autowired
    private Binding binding;

    @Autowired
    private DirectExchange birthdayExchange;

    @Autowired
    private Queue birthdayQueue;

    @Autowired
    private Binding birthdayBinding;

    @Autowired
    private TopicExchange couponIssuingStartedExchange;

    @Autowired
    private DirectExchange issueCouponsToUsersExchange;

    @Autowired
    private Queue issueCouponsToUsersQueue;

    @Autowired
    private Binding issueCouponsToUsersBinding;

    @MockBean
    private ConnectionFactory connectionFactory;

    @Test
    void testRabbitTemplateBean() {
        assertThat(rabbitTemplate).isNotNull();
    }

    @Test
    void testUserExchangeBean() {
        assertThat(userExchange).isNotNull();
        assertThat(userExchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    }

    @Test
    void testWelcomeCouponQueueBean() {
        assertThat(welcomeCouponQueue).isNotNull();
        assertThat(welcomeCouponQueue.getName()).isEqualTo(RabbitMQConfig.QUEUE_NAME);
    }

    @Test
    void testBindingBean() {
        assertThat(binding).isNotNull();
    }

    @Test
    void testBirthdayExchangeBean() {
        assertThat(birthdayExchange).isNotNull();
        assertThat(birthdayExchange.getName()).isEqualTo(RabbitMQConfig.BIRTHDAY_EXCHANGE_NAME);
    }

    @Test
    void testBirthdayQueueBean() {
        assertThat(birthdayQueue).isNotNull();
        assertThat(birthdayQueue.getName()).isEqualTo(RabbitMQConfig.BIRTHDAY_QUEUE_NAME);
    }

    @Test
    void testBirthdayBindingBean() {
        assertThat(birthdayBinding).isNotNull();
    }

    @Test
    void testCouponIssuingStartedExchangeBean() {
        assertThat(couponIssuingStartedExchange).isNotNull();
        assertThat(couponIssuingStartedExchange.getName()).isEqualTo(RabbitMQConfig.COUPON_ISSUING_STARTED_EXCHANGE);
    }

    @Test
    void testIssueCouponsToUsersExchangeBean() {
        assertThat(issueCouponsToUsersExchange).isNotNull();
        assertThat(issueCouponsToUsersExchange.getName()).isEqualTo(RabbitMQConfig.ISSUE_COUPONS_TO_USERS_EXCHANGE);
    }

    @Test
    void testIssueCouponsToUsersQueueBean() {
        assertThat(issueCouponsToUsersQueue).isNotNull();
        assertThat(issueCouponsToUsersQueue.getName()).isEqualTo(RabbitMQConfig.ISSUE_COUPONS_TO_USERS_QUEUE);
    }

    @Test
    void testIssueCouponsToUsersBindingBean() {
        assertThat(issueCouponsToUsersBinding).isNotNull();
    }
}
