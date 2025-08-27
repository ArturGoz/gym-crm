package com.gca.repository.impl;

import com.gca.model.Trainer;
import com.gca.model.TrainingType;
import com.gca.model.User;
import com.gca.repository.BaseIntegrationTest;
import com.gca.repository.TrainerRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataSet(value = "dataset/trainer/trainer-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
public class TrainerRepositoryTest extends BaseIntegrationTest<TrainerRepository> {

    @Test
    @DataSet(value = "dataset/trainer/trainer-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldCreateTrainer() {
        Trainer expected = buildTraineeForCreation();

        Trainer actual = repository.save(expected);

        assertNotNull(actual.getId(), "Created trainer should have ID");
        assertEquals(expected.getUser().getUsername(), actual.getUser().getUsername());
        assertEquals(expected.getSpecialization().getId(), actual.getSpecialization().getId());
        assertEquals(expected.getUser().getFirstName(), actual.getUser().getFirstName());
        assertEquals(expected.getUser().getLastName(), actual.getUser().getLastName());
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer expected = buildTrainer();
        expected.getUser().setFirstName("UpdatedName");

        Trainer actual = repository.save(expected);

        assertEquals(expected.getUser().getFirstName(), actual.getUser().getFirstName());
        assertEquals(expected.getSpecialization().getId(), actual.getSpecialization().getId());
        assertEquals(expected.getUser().getLastName(), actual.getUser().getLastName());
    }

    private Trainer buildTrainer() {
        return buildTraineeForCreation().toBuilder().id(1L).build();
    }

    private Trainer buildTraineeForCreation() {
        User user = User.builder()
                .firstName("Triple")
                .lastName("H")
                .username("triple.h")
                .password("123qwe123")
                .isActive(true)
                .build();

        TrainingType trainingType = TrainingType.builder()
                .id(1L)
                .name("Fitness")
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(trainingType)
                .build();
    }
}
