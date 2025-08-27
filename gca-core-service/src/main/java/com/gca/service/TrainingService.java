package com.gca.service;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.model.TrainingType;
import jakarta.validation.Valid;

import java.util.List;

public interface TrainingService {
    TrainingDTO createTraining(@Valid TrainingCreateDTO request);

    List<TrainingDTO> getTraineeTrainings(@Valid TrainingTraineeCriteriaFilter filter);

    List<TrainingDTO> getTrainerTrainings(@Valid TrainingTrainerCriteriaFilter filter);

    List<TrainingType> getAllTrainingTypes();
}