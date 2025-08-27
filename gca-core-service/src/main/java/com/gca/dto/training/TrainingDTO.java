package com.gca.dto.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {
    private String trainingName;
    private String trainerName;
    private String traineeName;
    private LocalDate trainingDate;
    private Long trainingDuration;
}


