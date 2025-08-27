package com.gca.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTrainersUpdateDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, max = 50, message = "Username must be 1-50 characters")
    private String traineeUsername;

    @NotNull
    private List<String> trainerNames;
}
