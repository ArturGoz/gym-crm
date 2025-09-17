package com.gca.integration.service;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.integration.sender.WorkloadSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerWorkloadService {

    private final WorkloadSender sender;

    public void notifyWorkloadService(TrainerWorkloadDTO request) {
        sender.processTrainerWorkloadRequest(request);
    }
}


