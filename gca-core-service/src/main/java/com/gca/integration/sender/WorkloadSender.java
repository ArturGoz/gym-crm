package com.gca.integration.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.exception.ServiceException;
import com.gca.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadSender {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final WorkloadMessageFactory messageFactory;

    @Value("${jms.queue.trainer-workload}")
    private String queueName;

    public void processTrainerWorkloadRequest(TrainerWorkloadDTO request) {
        String txId = extractTransactionId();
        log.info("Calling workload service with request: {}", request);

        String payload = serializeRequest(request);
        sendWithCircuitBreaker(payload, txId);
    }

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    protected void sendWithCircuitBreaker(String payload, String txId) {
        jmsTemplate.send(queueName, messageFactory.createMessage(payload, txId));
    }

    private void fallback(String payload, String txId, Throwable ex) {
        String message = format("Workload service unavailable. Reason: %s", ex.getMessage());
        throw new ServiceUnavailableException(message, ex);
    }

    private String serializeRequest(TrainerWorkloadDTO request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to serialize workload request", e);
        }
    }

    private String extractTransactionId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new IllegalStateException("No current HTTP request available");
        }

        return attrs.getRequest().getHeader(TRANSACTION_ID_HEADER);
    }
}