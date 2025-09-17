package com.gca.workloadservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.openapi.model.TrainerWorkloadRequest;
import jakarta.jms.Message;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadListenerTest {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String MDC_TRANSACTION_ID = "transactionId";

    @Mock
    private TrainerWorkloadDispatcher dispatcher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Message jmsMessage;

    @InjectMocks
    private TrainerWorkloadListener listener;

    @Test
    void onMessage_successfullyProcessesMessage() throws Exception {
        String payload = "{\"trainer\":\"John\"}";
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        when(jmsMessage.getJMSMessageID()).thenReturn("jms-123");
        when(jmsMessage.getStringProperty(TRANSACTION_ID_HEADER)).thenReturn("tx-456");
        when(objectMapper.readValue(payload, TrainerWorkloadRequest.class)).thenReturn(request);

        listener.onMessage(payload, jmsMessage);

        verify(dispatcher).dispatchTrainerWorkloadRequest(request);
        assertThat(MDC.get(MDC_TRANSACTION_ID)).isNull();
    }

    @Test
    void onMessage_missingTransactionId_putsNA() throws Exception {
        String payload = "{}";
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        when(jmsMessage.getJMSMessageID()).thenReturn("jms-1");
        when(jmsMessage.getStringProperty(TRANSACTION_ID_HEADER)).thenReturn(null);
        when(objectMapper.readValue(payload, TrainerWorkloadRequest.class)).thenReturn(request);

        listener.onMessage(payload, jmsMessage);

        verify(dispatcher).dispatchTrainerWorkloadRequest(request);
        assertThat(MDC.get(MDC_TRANSACTION_ID)).isNull();
    }

    @Test
    void onMessage_deserializationFails_throwsException() throws Exception {
        String payload = "{invalid}";

        when(jmsMessage.getJMSMessageID()).thenReturn("jms-err");
        when(jmsMessage.getStringProperty(TRANSACTION_ID_HEADER)).thenReturn("tx-err");
        when(objectMapper.readValue(payload, TrainerWorkloadRequest.class))
                .thenThrow(new JsonProcessingException("boom") {
                });

        Throwable thrown = catchThrowable(() -> listener.onMessage(payload, jmsMessage));

        assertThat(thrown)
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Failed to deserialize workload request");
        assertThat(MDC.get(MDC_TRANSACTION_ID)).isNull();
    }
}
