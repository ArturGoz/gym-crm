package com.gca.automation.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.automation.dto.TrainerWorkloadDTO;
import com.gca.automation.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final WorkloadMessageFactory messageFactory;

    @Value("${jms.queue.trainer-workload}")
    private String queueName;

    public void processTrainerWorkloadRequest(TrainerWorkloadDTO request) {
        String txId = UUID.randomUUID().toString();
        log.info("Calling workload service with request: {}", request);

        String payload = serializeRequest(request);
        jmsTemplate.send(queueName, messageFactory.createMessage(payload, txId));
    }

    private String serializeRequest(TrainerWorkloadDTO request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to serialize workload request", e);
        }
    }
}