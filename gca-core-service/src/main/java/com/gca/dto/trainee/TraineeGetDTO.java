package com.gca.dto.trainee;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.user.UserGetData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TraineeGetDTO extends UserGetData {
    private LocalDate dateOfBirth;
    private String address;
    private List<AssignedTrainerDTO> trainers;
}
