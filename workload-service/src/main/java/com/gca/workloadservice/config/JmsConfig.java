package com.gca.workloadservice.config;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
@Slf4j
public class JmsConfig {

    private final ConnectionFactory connectionFactory;

    public JmsConfig(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Value("${jms.listener.concurrency}") String concurrency) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency(concurrency);
        factory.setSessionTransacted(true);
        factory.setErrorHandler(t -> log.error("JMS listener error: {}", t.getMessage()));

        return factory;
    }
}