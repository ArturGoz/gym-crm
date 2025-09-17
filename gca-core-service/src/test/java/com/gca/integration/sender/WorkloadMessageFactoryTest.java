package com.gca.integration.sender;

import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.MessageCreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageFactoryTest {

    @Mock
    private Session session;

    @Mock
    private TextMessage textMessage;

    @InjectMocks
    private WorkloadMessageFactory factory;

    @Test
    void shouldCreateMessageWithTxId() throws Exception {
        String payload = "{\"key\":\"value\"}";
        String txId = "tx-123";

        when(session.createTextMessage(payload)).thenReturn(textMessage);

        MessageCreator actualCreator = factory.createMessage(payload, txId);

        Message actualMessage = actualCreator.createMessage(session);
        assertThat(actualMessage).isSameAs(textMessage);
        verify(session).createTextMessage(payload);
        verify(textMessage).setStringProperty("X-Transaction-Id", txId);
    }

    @Test
    void shouldCreateMessageWithoutTxId() throws Exception {
        String payload = "{\"key\":\"value\"}";

        when(session.createTextMessage(payload)).thenReturn(textMessage);

        MessageCreator actualCreator = factory.createMessage(payload, null);

        Message actualMessage = actualCreator.createMessage(session);
        assertThat(actualMessage).isSameAs(textMessage);
        verify(session).createTextMessage(payload);
        verify(textMessage, never()).setStringProperty(anyString(), any());
    }
}