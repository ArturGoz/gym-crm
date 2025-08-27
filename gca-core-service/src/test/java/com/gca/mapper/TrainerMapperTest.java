package com.gca.mapper;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.utils.GymTestProvider;
import com.gca.model.Trainer;
import com.gca.model.TrainingType;
import com.gca.model.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainerMapperTest {
    private static final Long USER_ID = 30L;
    private static final String USERNAME = "alice.smith";
    private static final String FIRSTNAME = "Alice";
    private static final String LASTNAME = "Smith";
    private static final boolean IS_ACTIVE = true;
    private static final Long SPECIALIZATION_ID = 42L;
    private static final String SPECIALIZATION_NAME = "Pilates";

    TrainerMapper mapper = new TrainerMapperImpl();

    @Test
    void testToEntity_fromUpdateResponse() {
        Trainer expected = GymTestProvider.constructTrainer();

        TrainerUpdateResponseDTO actual = mapper.toUpdateResponse(expected);

        assertNotNull(actual);
        assertEquals(expected.getUser().getLastName(), actual.getLastName());
        assertEquals(expected.getUser().getFirstName(), actual.getFirstName());
        assertEquals(expected.getUser().getUsername(), actual.getUsername());
        assertEquals(expected.getSpecialization().getName(), actual.getSpecialization());
        assertEquals(expected.getTrainees().size(), actual.getTrainees().size());
    }

    @Test
    void testToAssignedDto() {
        User user = buildUser();
        Trainer expected = Trainer.builder()
                .id(100L)
                .user(user)
                .specialization(buildTrainingType())
                .trainings(Collections.emptyList())
                .trainees(Collections.emptySet())
                .build();

        AssignedTrainerDTO actual = mapper.toAssignedDto(expected);

        assertNotNull(actual);
        assertEquals(expected.getUser().getUsername(), actual.getUsername());
        assertEquals(expected.getSpecialization().getName(), actual.getSpecialization());
    }

    private User buildUser() {
        return User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRSTNAME)
                .lastName(LASTNAME)
                .isActive(IS_ACTIVE)
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(SPECIALIZATION_ID)
                .name(TrainerMapperTest.SPECIALIZATION_NAME)
                .build();
    }
}
