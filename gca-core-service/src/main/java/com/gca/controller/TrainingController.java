package com.gca.controller;

import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.ErrorResponse;
import com.gca.openapi.model.TrainingCreateRequest;
import com.gca.openapi.model.TrainingTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gca.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH +"/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Operations related to training sessions and types")
public class TrainingController {
    private final TrainingAppFacade facade;

    @Operation(
            summary = "Add new training",
            description = "Creates a new training session with the specified trainee, trainer, date, and duration.",
            operationId = "addTraining"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> createTraining(
            @RequestBody TrainingCreateRequest request
    ) {
        facade.createTraining(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get training types",
            description = "Returns list of all available training types.",
            operationId = "getTrainingTypes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of training types",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainingTypeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Training types not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        List<TrainingTypeResponse> responses = facade.getAllTrainingTypes();
        return ResponseEntity.ok().body(responses);
    }
}
