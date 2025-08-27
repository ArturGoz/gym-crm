package com.gca.repository.impl;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.model.Training;
import com.gca.repository.BaseIntegrationTest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TrainingQueryRepositoryImpl.class)
@DataSet(value = "dataset/training/training-criteria-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TrainingQueryRepositoryImplTest extends BaseIntegrationTest<TrainingQueryRepositoryImpl> {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTrainerTrainingCriteria")
    void shouldFilterTrainerTrainingsByCriteria(String description,
                                                String username,
                                                LocalDate fromDate,
                                                LocalDate toDate,
                                                String traineeName,
                                                int expectedCount) {
        TrainingTrainerCriteriaFilter filter = TrainingTrainerCriteriaFilter.builder()
                .trainerUsername(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();

        List<Training> result = repository.findTrainingsForTrainer(filter);

        assertEquals(expectedCount, result.size(), description);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTraineeTrainingCriteria")
    void shouldFilterTraineeTrainingsByCriteria(String description,
                                                String username,
                                                LocalDate fromDate,
                                                LocalDate toDate,
                                                String trainerName,
                                                String trainingTypeName,
                                                int expectedCount) {
        TrainingTraineeCriteriaFilter filter = TrainingTraineeCriteriaFilter.builder()
                .traineeUsername(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingTypeName(trainingTypeName)
                .build();

        List<Training> result = repository.findTrainingsForTrainee(filter);

        assertEquals(expectedCount, result.size(), description);
    }

    static Stream<Arguments> provideTraineeTrainingCriteria() {
        return Stream.of(
                Arguments.of("All trainings for trainee", "arnold.schwarzenegger", null, null, null, null, 2),
                Arguments.of("Filter by existing trainer name", "arnold.schwarzenegger", null, null, "trainer.one", null, 2),
                Arguments.of("Filter by wrong trainer name", "arnold.schwarzenegger", null, null, "nonexistent", null, 0),
                Arguments.of("Filter by training type", "arnold.schwarzenegger", null, null, null, "Fitness", 1),
                Arguments.of("Filter by date range matching one", "arnold.schwarzenegger", LocalDate.of(2025, 7, 7), LocalDate.of(2025, 7, 7), null, null, 1),
                Arguments.of("Filter by date range not matching", "arnold.schwarzenegger", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 6), null, null, 0)
        );
    }

    static Stream<Arguments> provideTrainerTrainingCriteria() {
        return Stream.of(
                Arguments.of("All trainings for trainer", "trainer.one", null, null, null, 2),
                Arguments.of("Filter by existing trainee name", "trainer.one", null, null, "arnold.schwarzenegger", 2),
                Arguments.of("Filter by non-existing trainee name", "trainer.one", null, null, "nonexistent", 0),
                Arguments.of("Filter by date range matching one", "trainer.one", LocalDate.of(2025, 7, 7), LocalDate.of(2025, 7, 7), null, 1),
                Arguments.of("Filter by date range not matching", "trainer.one", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 6), null, 0)
        );
    }
}