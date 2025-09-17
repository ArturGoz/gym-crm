package com.gca.workloadservice.integration;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DLQListenerTest {

    @Mock
    private TextMessage textMessage;

    @InjectMocks
    private DLQListener listener;

    @Test
    void handleDeadLetter_textMessage_logsCorrectly() throws JMSException {
        when(textMessage.getText()).thenReturn("test DLQ message");

        listener.handleDeadLetter(textMessage);

        verify(textMessage).getText();
    }

    @Test
    void handleDeadLetter_nonTextMessage_logsError() throws JMSException {
        Message nonTextMessage = mock(Message.class);

        listener.handleDeadLetter(nonTextMessage);

        verify(textMessage, times(0)).getText();
    }

    @Test
    void handleDeadLetter_jmsException_logsError() throws JMSException {
        when(textMessage.getText()).thenThrow(new JMSException("JMS failed"));

        listener.handleDeadLetter(textMessage);

        verify(textMessage).getText();
    }
}
