package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataSet(value = "dataset/trainers-workload-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TrainerWorkloadRepositoryTest extends BaseIntegrationTest<TrainerWorkloadRepository> {

    @Test
    void shouldLoadAllTrainerWorkloads() {
        List<TrainerWorkload> actual = repository.findAll();

        assertThat(actual).hasSize(2);
        assertThat(actual)
                .extracting(TrainerWorkload::getUsername)
                .containsExactlyInAnyOrder("john.cena", "ronnie.coleman");
    }

    @Test
    void shouldFindTrainerById() {
        Optional<TrainerWorkload> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getFirstName()).isEqualTo("John");
        assertThat(actual.get().getLastName()).isEqualTo("Cena");
    }

    @Test
    @DataSet(value = "dataset/empty-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldSaveNewTrainer() {
        TrainerWorkload newTrainer1 = buildTrainerWorkload("Dwayne", "Johnson");
        TrainerWorkload newTrainer2 = buildTrainerWorkload("Tom", "Hardy");

        TrainerWorkload actualTrainer1 = repository.save(newTrainer1);
        TrainerWorkload actualTrainer2 = repository.save(newTrainer2);

        assertThat(actualTrainer1.getId()).isNotNull();
        assertThat(actualTrainer2.getId()).isNotNull();
        assertEquals(1L, actualTrainer1.getId());
        assertEquals(2L, actualTrainer2.getId());
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