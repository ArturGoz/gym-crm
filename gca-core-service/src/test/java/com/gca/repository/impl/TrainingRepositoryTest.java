
package com.gca.repository.impl;

import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.Training;
import com.gca.model.TrainingType;
import com.gca.model.User;
import com.gca.repository.BaseIntegrationTest;
import com.gca.repository.TrainingRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataSet(value = "dataset/training/training-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
public class TrainingRepositoryTest extends BaseIntegrationTest<TrainingRepository> {

    @Test
    @DataSet(value = "dataset/training/training-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldCreateTraining() {
        Training expected = buildTrainingFromExistingData();

        Training actual = repository.save(expected);

        assertNotNull(actual.getId(), "Created training should have ID");
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getTrainer().getId(), actual.getTrainer().getId());
        assertEquals(expected.getTrainee().getId(), actual.getTrainee().getId());
        assertEquals(expected.getType().getId(), actual.getType().getId());
    }

    private Training buildTrainingFromExistingData() {
        return Training.builder()
                .name("New Training Session")
                .date(LocalDate.of(2025, 7, 8))
                .duration(90L)
                .trainee(buildTrainee(buildUserArnold()))
                .trainer(buildTrainer(buildUserTrainerOne(), buildTrainingType()))
                .type(buildTrainingType())
                .build();
    }

    private User buildUserArnold() {
        return User.builder()
                .id(1L)
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .password("test123")
                .isActive(true)
                .build();
    }

    private Trainee buildTrainee(User user) {
        return Trainee.builder()
                .id(1L)
                .user(user)
                .address("123 Main St")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(1L)
                .name("Fitness")
                .build();
    }

    private User buildUserTrainerOne() {
        return User.builder()
                .id(2L)
                .firstName("Trainer")
                .lastName("One")
                .username("trainer.one")
                .password("trainerpass")
                .isActive(true)
                .build();
    }

    private Trainer buildTrainer(User user, TrainingType specialization) {
        return Trainer.builder()
                .id(1L)
                .user(user)
                .specialization(specialization)
                .build();
    }
}

