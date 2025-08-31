package com.gca.workloadservice.service.impl;

import com.gca.workloadservice.dto.TrainerWorkloadDTO;
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
    public TrainerWorkload addTrainingWorkload(@Valid TrainerWorkloadDTO dto) {
        log.debug("Add training workload");

        TrainerWorkload trainer = findOrCreateTrainer(dto);
        YearWorkload yearWorkload = findOrCreateYear(trainer, dto.getTrainingDate().getYear());
        MonthWorkload monthWorkload = findOrCreateMonth(yearWorkload, dto.getTrainingDate().getMonthValue());

        updateMonthDuration(monthWorkload, dto.getTrainingDuration());

        return repository.save(trainer);
    }

    @Override
    public void deleteTrainingWorkload(String username) {
        log.debug("Delete training workload");
        repository.deleteTrainerWorkloadsByUsername(username);
    }

    private TrainerWorkload findOrCreateTrainer(TrainerWorkloadDTO dto) {
        return repository.findTrainerWorkloadsByUsername(dto.getTrainerUsername())
                .orElseGet(() -> mapper.toEntity(dto));
    }

    private YearWorkload findOrCreateYear(TrainerWorkload trainer, int year) {
        return trainer.getYears().stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    YearWorkload newYear = buildYearWorkload(trainer, year);
                    trainer.getYears().add(newYear);

                    return newYear;
                });
    }

    private MonthWorkload findOrCreateMonth(YearWorkload yearWorkload, int month) {
        return yearWorkload.getMonths().stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthWorkload newMonth = buildMonthWorkload(yearWorkload, month);
                    yearWorkload.getMonths().add(newMonth);

                    return newMonth;
                });
    }

    private void updateMonthDuration(MonthWorkload monthWorkload, long duration) {
        monthWorkload.setTrainingSummaryDuration(monthWorkload.getTrainingSummaryDuration() + duration);
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
