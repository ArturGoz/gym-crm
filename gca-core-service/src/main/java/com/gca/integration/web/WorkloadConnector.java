package com.gca.integration.web;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.gca.controller.ApiConstant.TRAINER_WORKLOAD_URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadConnector {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallback")
    public ResponseEntity<Void> processTrainerWorkloadRequest(TrainerWorkloadDTO request) {
        log.info("Processing workload request for trainer: {}", request.getTrainerUsername());
        return restTemplate.postForEntity(TRAINER_WORKLOAD_URL, request, Void.class);
    }

    private ResponseEntity<Void> fallback(TrainerWorkloadDTO request, Throwable ex) {
        log.error("Workload service unavailable for trainer: {}, reason: {}",
                request.getTrainerUsername(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}

