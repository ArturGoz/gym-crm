package com.gca.dto.trainer;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadDTO {

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 100, message = "First name must be 1-100 characters")
    private String trainerUsername;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be 1-50 characters")
    private String trainerFirstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be 1-50 characters")
    private String trainerLastName;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Training date must be in future or present")
    private LocalDate trainingDate;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration must be positive")
    private Integer trainingDuration;

    @NotNull(message = "Active status cannot be null")
    private Boolean isActive;

    @NotNull(message = "Action type is required")
    private ActionType actionType;
}