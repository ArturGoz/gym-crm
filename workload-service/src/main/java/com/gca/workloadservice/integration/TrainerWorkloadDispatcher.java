package com.gca.workloadservice.integration;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadDispatcher {
    private final TrainerWorkloadService service;

    public void dispatchTrainerWorkloadRequest(TrainerWorkloadRequest request) {
        switch (request.getActionType()) {
            case ADD -> service.addTrainingWorkload(request);
            case DELETE -> service.deleteTrainingWorkload(request.getTrainerUsername());
        }
    }
}

