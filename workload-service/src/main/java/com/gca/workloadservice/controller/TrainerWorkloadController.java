package com.gca.workloadservice.controller;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.service.TrainerWorkloadService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Validated
    @GetMapping("/workload/{username}")
    public ResponseEntity<Long> getTrainerWorkload(
            @PathVariable @NotBlank String username,
            @RequestParam @Min(0) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        long workload = service.getTrainerWorkloadDurationSummary(username, year, month);

        return ResponseEntity.ok(workload);
    }

    private void processTrainerWorkload(TrainerWorkloadRequest request) {
        switch (request.getActionType()) {
            case ADD -> service.addTrainingWorkload(request);
            case DELETE -> service.deleteTrainingWorkload(request.getTrainerUsername());
        }
    }
}
