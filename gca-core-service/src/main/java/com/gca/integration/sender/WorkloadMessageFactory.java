package com.gca.integration.sender;

import jakarta.jms.TextMessage;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class WorkloadMessageFactory {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    public MessageCreator createMessage(String payload, String txId) {
        return session -> {
            TextMessage message = session.createTextMessage(payload);

            if (txId != null) {
                message.setStringProperty(TRANSACTION_ID_HEADER, txId);
            }

            return message;
        };
    }
}

