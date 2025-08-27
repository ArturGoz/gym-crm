package com.gca.controller;

import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.ActivationStatusRequest;
import com.gca.openapi.model.TrainerCreateRequest;
import com.gca.openapi.model.TrainerCreateResponse;
import com.gca.openapi.model.TrainerGetResponse;
import com.gca.openapi.model.TrainerUpdateRequest;
import com.gca.openapi.model.TrainerUpdateResponse;
import com.gca.openapi.model.TrainingGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.gca.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainingAppFacade facade;

    @PostMapping("/register")
    public ResponseEntity<TrainerCreateResponse> registerTrainer(@RequestBody TrainerCreateRequest trainer) {
        TrainerCreateResponse response = facade.createTrainer(trainer);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerGetResponse> getTrainerByUsername(@PathVariable(name = "username") String username) {
        TrainerGetResponse response = facade.getTrainerByUsername(username);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerUpdateResponse> updateTrainer(@PathVariable(name = "username") String username,
                                                               @RequestBody TrainerUpdateRequest trainer) {
        TrainerUpdateResponse response = facade.updateTrainer(username, trainer);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingGetResponse>> getTrainerTrainings(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(name = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(name = "traineeName", required = false) String traineeName) {

        TrainingTrainerCriteriaFilter criteria = new TrainingTrainerCriteriaFilter();
        criteria.setTrainerUsername(username);
        criteria.setFromDate(periodFrom);
        criteria.setToDate(periodTo);
        criteria.setTraineeName(traineeName);

        List<TrainingGetResponse> response = facade.findFilteredTrainings(criteria);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{username}/change-activation-status")
    public ResponseEntity<Void> updateTrainerActivationStatus(
            @PathVariable(name = "username") String username, @RequestBody ActivationStatusRequest request) {
        facade.toggleUserActiveStatus(username, request.getIsActive());

        return ResponseEntity.ok().build();
    }
}
