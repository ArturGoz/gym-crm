package com.gca.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JmsConfig {

    private final ConnectionFactory connectionFactory;

    public JmsConfig(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate(@Value("${jms.request-timeout-ms:5000}") long receiveTimeout) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setReceiveTimeout(receiveTimeout);

        return jmsTemplate;
    }
}
