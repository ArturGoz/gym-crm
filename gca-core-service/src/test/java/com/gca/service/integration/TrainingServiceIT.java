package com.gca.service.integration;

import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.mapper.TrainingMapper;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.TrainingType;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.repository.TrainingQueryRepository;
import com.gca.repository.TrainingRepository;
import com.gca.repository.TrainingTypeRepository;
import com.gca.service.impl.TrainingServiceImpl;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingServiceIT extends AbstractServiceIT {
    @Autowired
    private TrainingRepository dao;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TrainingMapper trainingMapper;

    @Autowired
    private TrainingQueryRepository queryRepository;

    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl(
                dao,
                trainerRepository,
                traineeRepository,
                trainingTypeRepository,
                queryRepository,
                trainingMapper
        );
    }

    @Test
    @DataSet(value = "dataset/training/training-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldCreateTraining() {
        Trainer trainer = trainerRepository.getReferenceById(1L);
        Trainee trainee = traineeRepository.getReferenceById(1L);
        TrainingType type = trainingTypeRepository.getReferenceById(1L);

        TrainingCreateDTO request = TrainingCreateDTO.builder()
                .trainerUsername(trainer.getUser().getUsername())
                .traineeUsername(trainee.getUser().getUsername())
                .trainingName(type.getName())
                .trainingDate(LocalDate.of(2025, 7, 10))
                .duration(60L)
                .build();

        TrainingDTO actual = trainingService.createTraining(request);

        assertEquals(type.getName(), actual.getTrainingName());
        assertEquals(trainer.getUser().getUsername(), actual.getTrainerName());
        assertEquals(trainee.getUser().getUsername(), actual.getTraineeName());
    }
}
