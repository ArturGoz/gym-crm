package com.gca.repository;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.model.Training;

import java.util.List;

public interface TrainingQueryRepository {
    List<Training> findTrainingsForTrainee(TrainingTraineeCriteriaFilter criteria);

    List<Training> findTrainingsForTrainer(TrainingTrainerCriteriaFilter criteria);
}