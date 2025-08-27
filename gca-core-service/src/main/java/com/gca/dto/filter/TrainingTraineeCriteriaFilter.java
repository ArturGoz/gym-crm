package com.gca.dto.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTraineeCriteriaFilter {

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Trainee username must not exceed 50 characters")
    private String traineeUsername;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Size(max = 50, message = "Trainer name must not exceed 50 characters")
    private String trainerName;

    @Size(max = 50, message = "Training type name must not exceed 50 characters")
    private String trainingTypeName;
}
