package com.gca.automation.dto;

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
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private Boolean isActive;
    private ActionType actionType;
}