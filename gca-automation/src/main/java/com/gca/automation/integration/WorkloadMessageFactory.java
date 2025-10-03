package com.gca.automation.integration;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageFactory {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    public MessageCreator createMessage(String payload, String txId) {
        return session -> buildTextMessage(session, payload, txId);
    }

    private TextMessage buildTextMessage(Session session, String payload, String txId) throws JMSException {
        TextMessage message = session.createTextMessage(payload);

        if (txId != null) {
            message.setStringProperty(TRANSACTION_ID_HEADER, txId);
        }

        return message;
    }
}

