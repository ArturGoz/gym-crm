package com.gca.workloadservice.service;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.model.TrainerWorkload;
import jakarta.validation.Valid;

public interface TrainerWorkloadService {
    TrainerWorkload addTrainingWorkload(@Valid TrainerWorkloadRequest request);

    long getTrainerWorkloadDurationSummary(String username, int year, int month);

    void deleteTrainingWorkload(String username);
}
