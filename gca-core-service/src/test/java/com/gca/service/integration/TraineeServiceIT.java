package com.gca.service.integration;

import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.mapper.TraineeMapper;
import com.gca.mapper.TrainerMapper;
import com.gca.mapper.UserMapper;
import com.gca.model.Trainee;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.service.UserService;
import com.gca.service.common.UserProfileService;
import com.gca.service.impl.TraineeServiceImpl;
import com.gca.utils.GymTestProvider;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeServiceIT extends AbstractServiceIT {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private TraineeMapper traineeMapper;

    @Autowired
    private TrainerMapper trainerMapper;

    @Autowired
    private TrainerRepository trainerRepository;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl(
                traineeRepository,
                trainerRepository,
                userService,
                userProfileService,
                traineeMapper,
                trainerMapper,
                userMapper,
                validator
        );
    }

    @Test
    @DataSet(value = "dataset/trainee/trainee-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldUpdateTrainee() {
        TraineeUpdateRequestDTO expected = GymTestProvider.createTraineeUpdateRequestDTO();

        TraineeUpdateResponseDTO actual = traineeService.updateTrainee(expected);

        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getIsActive(), actual.getIsActive());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    @DataSet(value = "dataset/trainee/trainee-creation-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldCreateTrainee() {
        TraineeCreateDTO request = GymTestProvider.createTraineeCreateDTO();

        UserCredentialsDTO actual = traineeService.createTrainee(request);

        String expected = format("%s.%s1", request.getFirstName(), request.getLastName()).toLowerCase();

        assertEquals(expected, actual.getUsername());
    }

    @Test
    @DataSet(value = "dataset/trainee/trainee-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
    void shouldDeleteTraineeByUsername() {
        String username = "arnold.schwarzenegger";

        traineeRepository.findByUserUsername(username).orElseThrow();
        traineeService.deleteTraineeByUsername(username);
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);

        assertThat(trainee).isEmpty();
    }
}

