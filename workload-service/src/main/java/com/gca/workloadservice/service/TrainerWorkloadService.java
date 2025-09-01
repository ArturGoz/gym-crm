package com.gca.workloadservice.service;

import com.gca.workloadservice.dto.TrainerWorkloadDTO;
import com.gca.workloadservice.model.TrainerWorkload;
import jakarta.validation.Valid;

public interface TrainerWorkloadService {
    TrainerWorkload addTrainingWorkload(@Valid TrainerWorkloadDTO trainerWorkloadDto);

    void deleteTrainingWorkload(String username);
}
