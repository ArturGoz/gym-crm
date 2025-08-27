package com.gca.dto.training;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class TrainingCreateDTO {
    @NotBlank(message = "Trainer username cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String trainerUsername;

    @NotBlank(message = "Trainee username cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String traineeUsername;

    @NotBlank(message = "Training name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be 1-100 characters")
    private String trainingName;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Training date must be in future or present")
    private LocalDate trainingDate;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be positive")
    private Long duration;
}
