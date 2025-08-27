package com.gca.repository.impl;

import com.gca.model.Trainee;
import com.gca.model.User;
import com.gca.repository.BaseIntegrationTest;
import com.gca.repository.TraineeRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataSet(value = "dataset/trainee/trainee-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TraineeRepositoryTest extends BaseIntegrationTest<TraineeRepository> {

    @Test
    @DataSet(value = "dataset/trainee/trainee-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldCreateTrainee() {
        Trainee expected = buildTraineeForCreation();

        Trainee actual = repository.save(expected);

        assertNotNull(actual.getId(), "Created expected should have ID");
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getUser().getUsername(), actual.getUser().getUsername());
        assertEquals(expected.getUser().getFirstName(), actual.getUser().getFirstName());
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee expected = buildTrainee();
        expected.setAddress("Updated Address");

        Trainee actual = repository.save(expected);

        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getUser(), actual.getUser());
        assertEquals(expected.getUser().getUsername(), actual.getUser().getUsername());
        assertEquals(expected.getUser().getFirstName(), actual.getUser().getFirstName());
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .isActive(true)
                .password("test123")
                .build();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .id(1L)
                .user(buildUser())
                .address("123 Main St")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    private User buildUserForCreation() {
        return User.builder()
                .firstName("Arnold22")
                .lastName("Schwarzenegger22")
                .username("Arnold22.Schwarzenegger22")
                .password("password")
                .isActive(true)
                .build();
    }

    private Trainee buildTraineeForCreation() {
        return Trainee.builder()
                .dateOfBirth(LocalDate.of(2000, 2, 2))
                .address("ADDRESS")
                .user(buildUserForCreation())
                .build();
    }
}

