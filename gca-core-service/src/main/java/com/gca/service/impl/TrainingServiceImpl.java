package com.gca.service.impl;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.mapper.TrainingMapper;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.Training;
import com.gca.model.TrainingType;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.repository.TrainingQueryRepository;
import com.gca.repository.TrainingRepository;
import com.gca.repository.TrainingTypeRepository;
import com.gca.service.TrainingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static java.lang.String.format;

@Service
@Validated
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingQueryRepository trainingQueryRepository;

    private final TrainingMapper trainingMapper;

    @Transactional
    @Override
    public TrainingDTO createTraining(@Valid TrainingCreateDTO request) {
        logger.debug("Creating training '{}'", request.getTrainingName());

        Training training = trainingMapper.toEntity(request);

        training.setTrainer(getTrainer(request.getTrainerUsername()));
        training.setTrainee(getTrainee(request.getTraineeUsername()));
        training.setType(getTrainingType(request.getTrainingName()));

        Training created = trainingRepository.save(training);

        logger.info("Created training: {}", created);
        return trainingMapper.toResponse(created);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingDTO> getTraineeTrainings(@Valid TrainingTraineeCriteriaFilter filter) {
        logger.debug("Filtering trainings by trainee");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainee(filter);

        return trainings.stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingDTO> getTrainerTrainings(@Valid TrainingTrainerCriteriaFilter filter) {
        logger.debug("Filtering trainings by trainer");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainer(filter);

        return trainings.stream()
                .map(trainingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingType> getAllTrainingTypes() {
        logger.debug("Getting all training types");
        return trainingTypeRepository.findAll();
    }

    private Trainer getTrainer(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Trainer with username %s not found", username)
                ));
    }

    private Trainee getTrainee(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Trainee with username %s not found", username)
                ));
    }

    private TrainingType getTrainingType(String username) {
        return trainingTypeRepository.findByName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Training type with name %s not found", username)
                ));
    }
}
