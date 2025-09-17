package com.gca.workloadservice.integration;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DLQListener {

    private static final String DLQ_NAME = "ActiveMQ.DLQ";

    @JmsListener(destination = DLQ_NAME)
    public void handleDeadLetter(Message message) {
        try {
            handleTextMessage(message);
        } catch (JMSException e) {
            log.error("Failed to read DLQ message text", e);
        }
    }

    private void handleTextMessage(Message message) throws JMSException {
        if (message instanceof TextMessage textMessage) {
            log.error("Message went to DLQ: {}, DLQ message text: {}", message, textMessage.getText());
            return;
        }

        log.error("DLQ message is not a TextMessage: {}", message);
    }
}
