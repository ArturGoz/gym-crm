package com.gca.repository.impl;

import com.gca.model.TrainingType;
import com.gca.repository.BaseIntegrationTest;
import com.gca.repository.TrainingTypeRepository;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataSet(value = "dataset/trainingtype/training-type-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TrainingTypeRepositoryTest extends BaseIntegrationTest<TrainingTypeRepository> {

    @Test
    void shouldFindAllTrainingTypes() {
        TrainingType yoga = bouldTrainingType(1L, "Yoga");
        TrainingType crossfit = bouldTrainingType(2L, "Crossfit");
        TrainingType pilates = bouldTrainingType(3L, "Pilates");
        List<TrainingType> expectedList = List.of(yoga, crossfit, pilates);

        List<TrainingType> actual = repository.findAll();

        assertNotNull(actual, "List must not be null");
        assertEquals(expectedList.size(), actual.size(), "Must return 3 training types");
    }

    private TrainingType bouldTrainingType(Long id, String name) {
        return TrainingType.builder()
                .id(id)
                .name(name)
                .build();
    }
}