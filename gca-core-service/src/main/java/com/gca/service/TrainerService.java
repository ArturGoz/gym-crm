package com.gca.service;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerCreateDTO;
import com.gca.dto.trainer.TrainerGetDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.dto.user.UserCredentialsDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface TrainerService {
    UserCredentialsDTO createTrainer(@Valid TrainerCreateDTO request);

    TrainerUpdateResponseDTO updateTrainer(@Valid TrainerUpdateRequestDTO request);

    TrainerGetDTO getTrainerByUsername(String username);

    List<AssignedTrainerDTO> getUnassignedTrainers(String traineeUsername);
}
