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
public class TrainingTrainerCriteriaFilter {

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Trainer username must not exceed 50 characters")
    private String trainerUsername;

    private LocalDate fromDate;
    private LocalDate toDate;

    @Size(max = 50, message = "Trainee name must not exceed 50 characters")
    private String traineeName;
}
