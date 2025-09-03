package com.gca.integration.service;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.exception.ServiceException;
import com.gca.integration.web.WorkloadConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final WorkloadConnector connector;

    public void addOrDeleteTrainerWorkload(TrainerWorkloadDTO request) {
        ResponseEntity<Void> response = connector.processTrainerWorkloadRequest(request);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ServiceException("Failed to process trainer workload request: " + response.getStatusCode());
        }
    }
}

