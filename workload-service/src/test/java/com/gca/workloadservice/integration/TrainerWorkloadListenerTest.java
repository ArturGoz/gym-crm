package com.gca.workloadservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.exception.ServiceException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;

import static com.gca.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.ADD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
    void onMessage_validRequest_success() throws Exception {
        TrainerWorkloadRequest request = buildValidTrainerWorkloadRequest();
        String payload = "{\"trainerUsername\":\"john.doe\",\"trainingDate\":\"2025-09-21\"," +
                "\"trainingDuration\":60,\"isActive\":true,\"actionType\":\"ADD\"}";

        when(jmsMessage.getJMSMessageID()).thenReturn("jms-123");
        when(jmsMessage.getStringProperty(TRANSACTION_ID_HEADER)).thenReturn("tx-456");
        when(objectMapper.readValue(payload, TrainerWorkloadRequest.class)).thenReturn(request);

        listener.onMessage(payload, jmsMessage);

        verify(dispatcher).dispatchTrainerWorkloadRequest(request);
        assertThat(MDC.get(MDC_TRANSACTION_ID)).isNull();
    }

    @Test
    void onMessage_invalidRequest_throwsConstraintViolationException() throws Exception {
        TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest();
        String payload = "{}";

        when(jmsMessage.getJMSMessageID()).thenReturn("jms-124");
        when(jmsMessage.getStringProperty(TRANSACTION_ID_HEADER)).thenReturn("tx-789");
        when(objectMapper.readValue(payload, TrainerWorkloadRequest.class)).thenReturn(invalidRequest);

        Throwable thrown = assertThrows(ConstraintViolationException.class, () ->
                listener.onMessage(payload, jmsMessage)
        );

        assertNotNull(thrown.getMessage());
        assertThat(MDC.get(MDC_TRANSACTION_ID)).isNull();
        verify(dispatcher, never()).dispatchTrainerWorkloadRequest(any());
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

    private TrainerWorkloadRequest buildValidTrainerWorkloadRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john.cena");
        request.setTrainerFirstName("John");
        request.setTrainerLastName("Cena");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60);
        request.setIsActive(true);
        request.setActionType(ADD);

        return request;
    }
}