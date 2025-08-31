package com.gca.workloadservice.repository;

import com.gca.workloadservice.model.MonthWorkload;
import com.gca.workloadservice.model.TrainerWorkload;
import com.gca.workloadservice.model.YearWorkload;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "dataset/month-workload-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class MonthWorkloadRepositoryTest extends BaseIntegrationTest<MonthWorkloadRepository> {

    @Test
    void shouldLoadAllMonthWorkloads() {
        List<MonthWorkload> actual = repository.findAll();

        assertThat(actual).hasSize(5);
        assertThat(actual)
                .extracting(MonthWorkload::getMonth)
                .containsExactlyInAnyOrder(1, 2, 12, 1, 3);
    }

    @Test
    void shouldFindMonthWorkloadById() {
        Optional<MonthWorkload> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getMonth()).isEqualTo(1);
        assertThat(actual.get().getTrainingSummaryDuration()).isEqualTo(120L);
    }

    @Test
    @DataSet(value = "dataset/month-workload-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldSaveNewMonthWorkload() {
        YearWorkload year = buildYearWorkload();
        MonthWorkload newMonth = MonthWorkload.builder()
                .month(5)
                .trainingSummaryDuration(180L)
                .yearWorkload(year)
                .build();

        MonthWorkload actual = repository.save(newMonth);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getMonth()).isEqualTo(5);
        assertThat(actual.getTrainingSummaryDuration()).isEqualTo(180L);
        assertThat(actual.getYearWorkload()).isEqualTo(year);
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

    private YearWorkload buildYearWorkload() {
        return YearWorkload.builder()
                .id(1L)
                .year(2025)
                .trainerWorkload(buildTrainerWorkload())
                .build();
    }
}