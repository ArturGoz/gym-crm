package com.gca.dto.trainee;

import com.gca.dto.trainer.AssignedTrainerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TraineeUpdateResponseDTO extends TraineeUpdateRequestDTO {
    private List<AssignedTrainerDTO> trainers;
}
