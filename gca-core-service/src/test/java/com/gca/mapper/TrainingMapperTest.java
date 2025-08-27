package com.gca.mapper;

import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.Training;
import com.gca.model.TrainingType;
import com.gca.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingMapperTest {
    private static final String TRAINER_USERNAME = "arnold.schwarzenegger";
    private static final String TRAINEE_USERNAME = "arnold.schwarzenegger1";
    private static final String TYPE_NAME = "Functional";
    private static final Long TYPE_ID = 1L;
    private static final String TYPE_NAME_YOGA = "Yoga";
    private static final Long TRAINER_ID = 100L;
    private static final Long TRAINEE_ID = 200L;

    TrainingMapper mapper = new TrainingMapperImpl();

    @Test
    void testToEntity_fromCreateRequest() {
        TrainingCreateDTO request = buildTrainingCreateDTO();

        Training actual = mapper.toEntity(request);

        assertEquals(LocalDate.of(2025, 6, 28), actual.getDate());
        assertEquals(90L, actual.getDuration());
        assertEquals(TYPE_NAME, actual.getName());
    }

    @Test
    void testToResponse() {
        Training training = buildTraining();

        TrainingDTO actual = mapper.toResponse(training);

        assertNotNull(actual);
        assertEquals(training.getName(), actual.getTrainingName());
        assertEquals(training.getDate(), actual.getTrainingDate());
        assertEquals(training.getTrainer().getUser().getUsername(), actual.getTrainerName());
    }

    private static TrainingCreateDTO buildTrainingCreateDTO() {
        return TrainingCreateDTO.builder()
                .trainerUsername(TRAINER_USERNAME)
                .traineeUsername(TRAINEE_USERNAME)
                .trainingName(TYPE_NAME)
                .trainingDate(LocalDate.of(2025, 6, 28))
                .duration(90L)
                .build();
    }

    private static Training buildTraining() {
        return Training.builder()
                .id(10L)
                .trainer(buildTrainer())
                .trainee(buildTrainee())
                .type(buildTrainingType())
                .date(LocalDate.of(2025, 6, 29))
                .duration(120L)
                .name("Morning Yoga")
                .build();
    }

    private static Trainer buildTrainer() {
        return Trainer.builder()
                .id(TRAINER_ID)
                .user(buildUser(TRAINER_ID, "arnold.schwarzenegger"))
                .build();
    }

    private static Trainee buildTrainee() {
        return Trainee.builder()
                .id(TRAINEE_ID)
                .user(buildUser(TRAINEE_ID, "ronnie.coleman"))
                .build();
    }

    private static TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(TYPE_ID)
                .name(TYPE_NAME_YOGA)
                .build();
    }

    private static User buildUser(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }
}
