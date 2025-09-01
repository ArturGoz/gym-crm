package com.gca.workloadservice.service.impl;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.mapper.TrainerWorkloadMapper;
import com.gca.workloadservice.model.MonthWorkload;
import com.gca.workloadservice.model.TrainerWorkload;
import com.gca.workloadservice.model.YearWorkload;
import com.gca.workloadservice.repository.TrainerWorkloadRepository;
import com.gca.workloadservice.service.TrainerWorkloadService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerWorkloadRepository repository;
    private final TrainerWorkloadMapper mapper;

    @Override
    public TrainerWorkload addTrainingWorkload(@Valid TrainerWorkloadRequest request) {
        log.info("Add training workload for username: {}", request.getTrainerUsername());

        TrainerWorkload trainer = findOrCreateTrainer(request);
        YearWorkload yearWorkload = findOrCreateYear(trainer, request.getTrainingDate().getYear());
        MonthWorkload monthWorkload = findOrCreateMonth(yearWorkload,
                request.getTrainingDate().getMonthValue());

        updateMonthDuration(monthWorkload, request.getTrainingDuration());

        return repository.save(trainer);
    }

    @Override
    public void deleteTrainingWorkload(String username) {
        log.info("Delete training workload for username: {}", username);
        repository.deleteTrainerWorkloadsByUsername(username);
    }

    private TrainerWorkload findOrCreateTrainer(TrainerWorkloadRequest request) {
        return repository.findTrainerWorkloadsByUsername(request.getTrainerUsername())
                .orElseGet(() -> mapper.toEntity(request));
    }

    private YearWorkload findOrCreateYear(TrainerWorkload trainer, int year) {
        return trainer.getYears().stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> createAndAddYear(trainer, year));
    }

    private YearWorkload createAndAddYear(TrainerWorkload trainer, int year) {
        log.warn("Year {} not found in workload. Creating new entry.", year);
        YearWorkload newYear = buildYearWorkload(trainer, year);
        trainer.getYears().add(newYear);

        return newYear;
    }

    private MonthWorkload findOrCreateMonth(YearWorkload yearWorkload, int month) {
        return yearWorkload.getMonths().stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> createAndAddMonth(yearWorkload, month));
    }

    private MonthWorkload createAndAddMonth(YearWorkload yearWorkload, int month) {
        log.warn("Month {} not found in workload. Creating new entry.", month);
        MonthWorkload newMonth = buildMonthWorkload(yearWorkload, month);
        yearWorkload.getMonths().add(newMonth);

        return newMonth;
    }

    private void updateMonthDuration(MonthWorkload monthWorkload, long duration) {
        long currentDuration = monthWorkload.getTrainingSummaryDuration();
        monthWorkload.setTrainingSummaryDuration(currentDuration + duration);
    }

    private YearWorkload buildYearWorkload(TrainerWorkload trainerWorkload, Integer year) {
        return YearWorkload.builder()
                .year(year)
                .trainerWorkload(trainerWorkload)
                .build();
    }

    private MonthWorkload buildMonthWorkload(YearWorkload yearWorkload, Integer month) {
        return MonthWorkload.builder()
                .month(month)
                .trainingSummaryDuration(0L)
                .yearWorkload(yearWorkload)
                .build();
    }
}
