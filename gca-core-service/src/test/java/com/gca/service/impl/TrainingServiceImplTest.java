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
import com.gca.utils.GymTestProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository repository;

    @Mock
    private TrainingMapper mapper;

    @Mock
    private TrainingQueryRepository queryRepository;

    @InjectMocks
    private TrainingServiceImpl service;

    @Test
    void createTraining_success() {
        TrainingCreateDTO request = GymTestProvider.createTrainingCreateRequestDTO();
        Training training = GymTestProvider.constructTrainingWithoutId();
        Training created = GymTestProvider.constructTraining();
        TrainingDTO expected = GymTestProvider.createTrainingDTO();

        Trainer trainer = GymTestProvider.constructTrainer();
        Trainee trainee = GymTestProvider.constructTrainee();
        TrainingType type = GymTestProvider.createTrainingTypeStrength();

        when(mapper.toEntity(request)).thenReturn(training);
        when(trainerRepository.findByUserUsername(request.getTrainerUsername())).
                thenReturn(ofNullable(trainer));
        when(traineeRepository.findByUserUsername(request.getTraineeUsername()))
                .thenReturn(ofNullable(trainee));
        when(trainingTypeRepository.findByName(request.getTrainingName()))
                .thenReturn(ofNullable(type));

        when(repository.save(any(Training.class))).thenReturn(created);
        when(mapper.toResponse(any(Training.class))).thenReturn(expected);

        TrainingDTO actual = service.createTraining(request);

        assertEquals(expected, actual);
        verify(mapper).toEntity(request);
        verify(trainerRepository).findByUserUsername(anyString());
        verify(traineeRepository).findByUserUsername(anyString());
        verify(trainingTypeRepository).findByName(anyString());
        verify(repository).save(any(Training.class));
        verify(mapper).toResponse(any(Training.class));
    }

    @Test
    void getTrainerTrainings_shouldReturnList_whenValidFilter() {
        TrainingTrainerCriteriaFilter filter = GymTestProvider.buildTrainerCriteriaFilter();
        Training training = GymTestProvider.constructTraining();
        List<Training> trainings = Collections.singletonList(training);
        TrainingDTO response = GymTestProvider.createTrainingDTO();

        when(queryRepository.findTrainingsForTrainer(filter)).thenReturn(trainings);
        when(mapper.toResponse(training)).thenReturn(response);

        List<TrainingDTO> actual = service.getTrainerTrainings(filter);

        assertEquals(1, actual.size());
        assertEquals(response, actual.get(0));
        verify(queryRepository).findTrainingsForTrainer(filter);
        verify(mapper).toResponse(training);
    }

    @Test
    void getTraineeTrainings_shouldReturnList_whenValidFilter() {
        TrainingTraineeCriteriaFilter filter = GymTestProvider.buildTraineeCriteriaFilter();
        Training training = GymTestProvider.constructTraining();
        List<Training> trainings = Collections.singletonList(training);
        TrainingDTO expected = GymTestProvider.createTrainingDTO();

        when(queryRepository.findTrainingsForTrainee(filter)).thenReturn(trainings);
        when(mapper.toResponse(training)).thenReturn(expected);

        List<TrainingDTO> actual = service.getTraineeTrainings(filter);

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
        verify(queryRepository).findTrainingsForTrainee(filter);
        verify(mapper).toResponse(training);
    }

    @Test
    void createTraining_shouldThrow_whenTrainerNotFound() {
        TrainingCreateDTO request = GymTestProvider.createTrainingCreateRequestDTO();

        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.createTraining(request));

        assertTrue(ex.getMessage().contains("Trainer with username"));
    }

    @Test
    void createTraining_shouldThrow_whenTrainingTypeNotFound() {
        TrainingCreateDTO request = GymTestProvider.createTrainingCreateRequestDTO();

        when(mapper.toEntity(request)).thenReturn(new Training());
        when(trainerRepository.findByUserUsername(request.getTrainerUsername()))
                .thenReturn(ofNullable(GymTestProvider.constructTrainer()));
        when(traineeRepository.findByUserUsername(request.getTraineeUsername()))
                .thenReturn(ofNullable(GymTestProvider.constructTrainee()));
        when(trainingTypeRepository.findByName(request.getTrainingName())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.createTraining(request));

        assertTrue(ex.getMessage().contains("Training type with name"));
    }

    @Test
    void createTraining_shouldThrow_whenTraineeNotFound() {
        TrainingCreateDTO request = GymTestProvider.createTrainingCreateRequestDTO();

        when(mapper.toEntity(request)).thenReturn(new Training());
        when(trainerRepository.findByUserUsername(request.getTrainerUsername()))
                .thenReturn(ofNullable(GymTestProvider.constructTrainer()));
        when(traineeRepository.findByUserUsername(request.getTraineeUsername())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.createTraining(request));

        assertTrue(ex.getMessage().contains("Trainee with username"));
    }
}

