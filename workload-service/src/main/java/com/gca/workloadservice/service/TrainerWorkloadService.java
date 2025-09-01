package com.gca.workloadservice.service;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.model.TrainerWorkload;
import jakarta.validation.Valid;

public interface TrainerWorkloadService {
    TrainerWorkload addTrainingWorkload(@Valid TrainerWorkloadRequest request);

    void deleteTrainingWorkload(String username);
}
