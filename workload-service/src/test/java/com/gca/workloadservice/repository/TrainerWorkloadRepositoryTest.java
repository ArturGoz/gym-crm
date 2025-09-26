package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadRepositoryTest extends BaseIntegrationTest<TrainerWorkloadRepository> {

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        TrainerWorkload trainer1 = buildTrainerWorkload("John", "Cena");
        TrainerWorkload trainer2 = buildTrainerWorkload("Ronnie", "Coleman");

        repository.saveAll(List.of(trainer1, trainer2));
    }


    @Test
    void shouldFindTrainerByUsername() {
        Optional<TrainerWorkload> actual = repository.findTrainerWorkloadsByUsername("john.cena");

        assertThat(actual).isPresent();
        assertThat(actual.get().getFirstName()).isEqualTo("John");
        assertThat(actual.get().getLastName()).isEqualTo("Cena");
    }

    @Test
    void shouldReturnEmptyIfTrainerUsernameNotExists() {
        Optional<TrainerWorkload> actual = repository.findTrainerWorkloadsByUsername("non.existent");

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        repository.deleteTrainerWorkloadsByUsername("ronnie.coleman");

        Optional<TrainerWorkload> result = repository.findTrainerWorkloadsByUsername("ronnie.coleman");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldLoadAllTrainerWorkloads() {
        List<TrainerWorkload> actual = repository.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual)
                .extracting(TrainerWorkload::getUsername)
                .containsExactlyInAnyOrder("john.cena", "ronnie.coleman");
    }

    @Test
    void shouldSaveNewTrainer() {
        TrainerWorkload newTrainer1 = buildTrainerWorkload("Dwayne", "Johnson");
        TrainerWorkload newTrainer2 = buildTrainerWorkload("Tom", "Hardy");

        TrainerWorkload actualTrainer1 = repository.save(newTrainer1);
        TrainerWorkload actualTrainer2 = repository.save(newTrainer2);

        assertThat(actualTrainer1.getId()).isNotNull();
        assertThat(actualTrainer2.getId()).isNotNull();
        assertEquals(newTrainer1.getUsername(), actualTrainer1.getUsername());
        assertEquals(newTrainer2.getUsername(), actualTrainer2.getUsername());
    }

    private TrainerWorkload buildTrainerWorkload(String firstName, String lastName) {
        return TrainerWorkload.builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .username(format("%s.%s", firstName, lastName).toLowerCase())
                .build();
    }
}