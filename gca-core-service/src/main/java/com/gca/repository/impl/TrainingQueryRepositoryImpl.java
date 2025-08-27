package com.gca.repository.impl;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.model.Training;
import com.gca.repository.TrainingQueryRepository;
import com.gca.repository.TrainingRepository;
import com.gca.repository.criteria.TraineeTrainingSpecification;
import com.gca.repository.criteria.TrainerTrainingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingQueryRepositoryImpl implements TrainingQueryRepository {

    private final TrainingRepository trainingRepository;

    @Override
    public List<Training> findTrainingsForTrainee(TrainingTraineeCriteriaFilter criteria) {
        return trainingRepository.findAll(TraineeTrainingSpecification.findByCriteria(criteria));
    }

    @Override
    public List<Training> findTrainingsForTrainer(TrainingTrainerCriteriaFilter criteria) {
        return trainingRepository.findAll(TrainerTrainingSpecification.findByCriteria(criteria));
    }
}

