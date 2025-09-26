package com.gca.workloadservice.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.exception.ServiceException;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadListener {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String MDC_TRANSACTION_ID = "transactionId";

    private final TrainerWorkloadDispatcher dispatcher;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "${jms.queue.trainer-workload}",
            containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String payload, Message rawMessage) {
        JmsProperties props = extractJmsProperties(rawMessage);
        putTransactionIdToMdc(props.transactionId());

        try {
            TrainerWorkloadRequest request = deserializeRequest(payload);
            logReceivedMessage(props.jmsId(), request);
            validateRequest(request);

            dispatcher.dispatchTrainerWorkloadRequest(request);
        } finally {
            clearMdc();
        }
    }

    private record JmsProperties(String jmsId, String transactionId) {
    }

    private JmsProperties extractJmsProperties(Message message) {
        if (message == null) {
            return new JmsProperties(null, null);
        }
        try {
            return new JmsProperties(
                    message.getJMSMessageID(),
                    message.getStringProperty(TRANSACTION_ID_HEADER)
            );
        } catch (JMSException ex) {
            log.warn("Error while reading JMS message properties: {}", ex.getMessage());
            return new JmsProperties(null, null);
        }
    }

    private TrainerWorkloadRequest deserializeRequest(String payload) {
        try {
            return objectMapper.readValue(payload, TrainerWorkloadRequest.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to deserialize workload request", e);
        }
    }

    private void putTransactionIdToMdc(String transactionId) {
        if (transactionId == null) {
            log.warn("Transaction ID is missing from JMS message");
            MDC.put(MDC_TRANSACTION_ID, "N/A");
        } else {
            MDC.put(MDC_TRANSACTION_ID, transactionId);
        }
    }

    private void clearMdc() {
        MDC.remove(MDC_TRANSACTION_ID);
    }

    private void validateRequest(TrainerWorkloadRequest request) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void logReceivedMessage(String jmsId, TrainerWorkloadRequest dto) {
        log.info("Received workload message id={} payload={}", jmsId, dto);
    }
}
