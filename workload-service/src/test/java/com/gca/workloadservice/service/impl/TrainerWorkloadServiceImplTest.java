package com.gca.workloadservice.service.impl;

import com.gca.workloadservice.dto.TrainerWorkloadDTO;
import com.gca.workloadservice.mapper.TrainerWorkloadMapper;
import com.gca.workloadservice.model.MonthWorkload;
import com.gca.workloadservice.model.TrainerWorkload;
import com.gca.workloadservice.model.YearWorkload;
import com.gca.workloadservice.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @Mock
    private TrainerWorkloadMapper mapper;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    @Test
    void addTrainingWorkload_NewTrainer_CreatesTrainerYearMonth() {
        TrainerWorkloadDTO request = buildArnoldSchwarzeneggerWorkload();

        TrainerWorkload expectedTrainer = TrainerWorkload.builder()
                .username(request.getTrainerUsername())
                .firstName(request.getTrainerFirstName())
                .lastName(request.getTrainerLastName())
                .isActive(request.getIsActive())
                .years(new ArrayList<>())
                .build();

        when(repository.findTrainerWorkloadsByUsername(request.getTrainerUsername()))
                .thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(expectedTrainer);
        when(repository.save(any(TrainerWorkload.class))).thenReturn(expectedTrainer);

        TrainerWorkload actualTrainer = service.addTrainingWorkload(request);

        assertThat(actualTrainer.getUsername()).isEqualTo(expectedTrainer.getUsername());
        assertThat(actualTrainer.getYears()).hasSize(1);
        YearWorkload actualYear = actualTrainer.getYears().get(0);
        assertThat(actualYear.getYear()).isEqualTo(2025);
        assertThat(actualYear.getMonths()).hasSize(1);

        MonthWorkload actualMonth = actualYear.getMonths().get(0);
        assertThat(actualMonth.getMonth()).isEqualTo(1);
        assertThat(actualMonth.getTrainingSummaryDuration()).isEqualTo(120L);
        verify(repository).save(expectedTrainer);
    }

    @Test
    void addTrainingWorkload_ExistingTrainerAndYear_AddsToMonth() {
        TrainerWorkloadDTO request = buildRonnieColemanWorkload();
        TrainerWorkload existingTrainer = buildExistingTrainerWithYearAndMonth(request);

        when(repository.findTrainerWorkloadsByUsername(request.getTrainerUsername()))
                .thenReturn(Optional.of(existingTrainer));
        when(repository.save(any(TrainerWorkload.class))).thenReturn(existingTrainer);

        TrainerWorkload actualTrainer = service.addTrainingWorkload(request);

        MonthWorkload actualMonth = actualTrainer.getYears().get(0).getMonths().get(0);
        assertThat(actualMonth.getTrainingSummaryDuration()).isEqualTo(300L);
        verify(repository).save(existingTrainer);
    }

    @Test
    void deleteTrainingWorkload_CallsRepositoryDelete() {
        String username = "arnold.schwarzenegger";
        service.deleteTrainingWorkload(username);
        verify(repository, times(1)).deleteTrainerWorkloadsByUsername(username);
    }

    private TrainerWorkload buildExistingTrainerWithYearAndMonth(TrainerWorkloadDTO request) {
        MonthWorkload existingMonth = MonthWorkload.builder()
                .month(3)
                .trainingSummaryDuration(100L)
                .build();

        YearWorkload existingYear = YearWorkload.builder()
                .year(2025)
                .months(new ArrayList<>(List.of(existingMonth)))
                .build();
        existingMonth.setYearWorkload(existingYear);

        TrainerWorkload existingTrainer = TrainerWorkload.builder()
                .username(request.getTrainerUsername())
                .years(new ArrayList<>(List.of(existingYear)))
                .build();
        existingYear.setTrainerWorkload(existingTrainer);

        return existingTrainer;
    }

    private TrainerWorkloadDTO buildRonnieColemanWorkload() {
        return TrainerWorkloadDTO.builder()
                .trainerUsername("ronnie.coleman")
                .trainerFirstName("Ronnie")
                .trainerLastName("Coleman")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 3, 15))
                .trainingDuration(200L)
                .build();
    }

    private TrainerWorkloadDTO buildArnoldSchwarzeneggerWorkload() {
        return TrainerWorkloadDTO.builder()
                .trainerUsername("arnold.schwarzenegger")
                .trainerFirstName("Arnold")
                .trainerLastName("Schwarzenegger")
                .isActive(true)
                .trainingDate(LocalDate.of(2025, 1, 10))
                .trainingDuration(120L)
                .build();
    }
}