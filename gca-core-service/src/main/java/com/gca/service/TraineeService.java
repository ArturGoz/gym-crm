package com.gca.service;

import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeTrainersUpdateDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.user.UserCredentialsDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface TraineeService {
    UserCredentialsDTO createTrainee(@Valid TraineeCreateDTO request);

    TraineeUpdateResponseDTO updateTrainee(@Valid TraineeUpdateRequestDTO request);

    TraineeGetDTO getTraineeByUsername(String username);

    List<AssignedTrainerDTO> updateTraineeTrainers(@Valid TraineeTrainersUpdateDTO request);

    void deleteTraineeByUsername(String username);
}
