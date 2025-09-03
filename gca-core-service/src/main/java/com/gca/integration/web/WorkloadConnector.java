package com.gca.integration.web;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.gca.controller.ApiConstant.TRAINER_WORKLOAD_URL;

@Component
@RequiredArgsConstructor
public class WorkloadConnector {

    private final RestTemplate restTemplate;

    public ResponseEntity<Void> processTrainerWorkloadRequest(TrainerWorkloadDTO request) {
        return restTemplate.postForEntity(
                TRAINER_WORKLOAD_URL,
                request,
                Void.class
        );
    }
}

