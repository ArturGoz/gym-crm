package com.gca.workloadservice.controller;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.gca.workloadservice.controller.ApiConstant.BASE_PATH;

@Controller
@RequestMapping(BASE_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerWorkloadController {

    private final TrainerWorkloadService service;

    @PostMapping("/workload")
    public ResponseEntity<Void> addOrDeleteTrainerWorkload(@RequestBody TrainerWorkloadRequest request) {
        processTrainerWorkload(request);

        return ResponseEntity.ok().build();
    }

    private void processTrainerWorkload(TrainerWorkloadRequest request) {
        switch (request.getActionType()) {
            case ADD -> service.addTrainingWorkload(request);
            case DELETE -> service.deleteTrainingWorkload(request.getTrainerUsername());
        }
    }
}
