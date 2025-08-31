package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.TrainerWorkload;
import com.gca.workloadservice.model.YearWorkload;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "dataset/year-workload-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class YearWorkloadRepositoryTest extends BaseIntegrationTest<YearWorkloadRepository> {

    @Test
    void shouldLoadAllYearWorkloads() {
        List<YearWorkload> actual = repository.findAll();

        assertThat(actual).hasSize(3);
        assertThat(actual)
                .extracting(YearWorkload::getYear)
                .containsExactlyInAnyOrder(2025, 2024, 2025);
    }

    @Test
    void shouldFindYearWorkloadById() {
        Optional<YearWorkload> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getYear()).isEqualTo(2025);
        assertThat(actual.get().getTrainerWorkload().getUsername()).isEqualTo("john.cena");
    }

    @Test
    @DataSet(value = "dataset/year-workload-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldSaveNewYearWorkload() {
        YearWorkload newYear = YearWorkload.builder()
                .year(2026)
                .trainerWorkload(buildTrainerWorkload())
                .build();

        YearWorkload actual = repository.save(newYear);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getYear()).isEqualTo(2026);
        assertThat(actual.getTrainerWorkload().getUsername()).isEqualTo("john.cena");
    }

    private TrainerWorkload buildTrainerWorkload() {
        return TrainerWorkload.builder()
                .id(1L)
                .firstName("John")
                .lastName("Cena")
                .username("john.cena")
                .isActive(true)
                .build();
    }
}