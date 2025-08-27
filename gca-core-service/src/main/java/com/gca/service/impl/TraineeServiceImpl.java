package com.gca.service.impl;

import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeTrainersUpdateDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.exception.ServiceException;
import com.gca.mapper.TraineeMapper;
import com.gca.mapper.TrainerMapper;
import com.gca.mapper.UserMapper;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.User;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.service.TraineeService;
import com.gca.service.UserService;
import com.gca.service.common.CoreValidator;
import com.gca.service.common.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;

@Service
@Validated
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String TRAINEE_NOT_FOUND_MSG = "Trainee with username '%s' not found";

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final UserProfileService userProfileService;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final UserMapper userMapper;
    private final CoreValidator validator;

    @Transactional
    @Override
    public UserCredentialsDTO createTrainee(@Valid TraineeCreateDTO request) {
        logger.debug("Creating trainee");

        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User user = userService.createUser(userCreateDTO);

        String rawPass = user.getPassword();
        user.setPassword(userProfileService.encryptPassword(rawPass));

        Trainee trainee = traineeMapper.toEntity(request).toBuilder()
                .user(user)
                .build();

        Trainee created = traineeRepository.save(trainee);

        logger.info("Created trainee: {}", created);
        return userMapper.toResponse(user.toBuilder().password(rawPass).build());
    }

    @Transactional
    @Override
    public TraineeUpdateResponseDTO updateTrainee(@Valid TraineeUpdateRequestDTO request) {
        logger.debug("Updating trainee");

        Trainee trainee = traineeRepository.findByUserUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(
                        format("Invalid trainee username: %s", request.getUsername())
                ));

        User filledUser = traineeMapper.fillUserFields(trainee.getUser(), request);
        Trainee filledTrainee = traineeMapper.fillTraineeFields(trainee, filledUser, request);

        Trainee updated = traineeRepository.save(filledTrainee);

        logger.info("Updated trainee: {}", updated);
        return traineeMapper.toUpdateResponse(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public TraineeGetDTO getTraineeByUsername(String username) {
        logger.debug("Getting trainee by username");

        validator.validateUsername(username);

        return traineeRepository.findByUserUsername(username)
                .map(trainee -> {
                    logger.debug("Trainee found by username: {}", username);
                    return traineeMapper.toGetDto(trainee);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        format(TRAINEE_NOT_FOUND_MSG, username)
                ));
    }


    @Transactional
    @Override
    public List<AssignedTrainerDTO> updateTraineeTrainers(@Valid TraineeTrainersUpdateDTO request) {
        logger.debug("Updating trainers for trainee username: {}", request.getTraineeUsername());

        Trainee trainee = traineeRepository.findByUserUsername(request.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException(
                        format(TRAINEE_NOT_FOUND_MSG, request.getTraineeUsername())
                ));

        trainee.setTrainers(new HashSet<>(getTrainerList(request.getTrainerNames())));
        Trainee updated = traineeRepository.save(trainee);

        logger.info("Updated trainers for trainee");
        return updated.getTrainers().stream()
                .map(trainerMapper::toAssignedDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteTraineeByUsername(String username) {
        logger.debug("Deleting trainee by username: {}", username);

        validator.validateUsername(username);

        traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        format(TRAINEE_NOT_FOUND_MSG, username)
                ));

        traineeRepository.deleteByUsername(username);
    }

    private List<Trainer> getTrainerList(List<String> usernames) {
        return usernames.stream()
                .map(name -> trainerRepository.findByUserUsername(name)
                        .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + name)))
                .toList();
    }
}

