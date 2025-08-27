package com.gca.service.impl;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerCreateDTO;
import com.gca.dto.trainer.TrainerGetDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.exception.ServiceException;
import com.gca.mapper.TrainerMapper;
import com.gca.mapper.UserMapper;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.TrainingType;
import com.gca.model.User;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.repository.TrainingTypeRepository;
import com.gca.service.TrainerService;
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

import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Service
@Validated
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserService userService;
    private final UserProfileService userProfileService;

    private final UserMapper userMapper;
    private final TrainerMapper trainerMapper;
    private final CoreValidator validator;

    @Transactional
    @Override
    public UserCredentialsDTO createTrainer(@Valid TrainerCreateDTO request) {
        logger.debug("Creating trainer");

        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User user = userService.createUser(userCreateDTO);

        String rawPass = user.getPassword();
        user.setPassword(userProfileService.encryptPassword(rawPass));

        TrainingType trainingType = trainingTypeRepository.findByName(request.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("Invalid training type"));

        Trainer trainer = Trainer.builder()
                .specialization(trainingType)
                .user(user)
                .build();

        Trainer created = trainerRepository.save(trainer);

        logger.info("Trainer created: {}", created);
        return userMapper.toResponse(user.toBuilder().password(rawPass).build());
    }

    @Transactional
    @Override
    public TrainerUpdateResponseDTO updateTrainer(@Valid TrainerUpdateRequestDTO request) {
        logger.debug("Updating trainer");

        Trainer trainer = trainerRepository.findByUserUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(
                        format("Invalid trainer username: %s", request.getUsername())
                ));

        User updatedUser = trainerMapper.fillUserFields(trainer.getUser(), request);

        TrainingType trainingType = trainingTypeRepository.findByName(request.getSpecialization())
                .orElse(trainer.getSpecialization());

        Trainer updatedTrainer = trainerMapper.fillTrainerFields(trainer, updatedUser, trainingType);
        Trainer updated = trainerRepository.save(updatedTrainer);

        logger.info("Trainer updated: {}", updated);
        return trainerMapper.toUpdateResponse(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerGetDTO getTrainerByUsername(String username) {
        logger.debug("Getting trainer by username");

        validator.validateUsername(username);

        return trainerRepository.findByUserUsername(username)
                .map(trainer -> {
                    logger.debug("Trainer found by username: {}", username);
                    return trainerMapper.toGetDto(trainer);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        format("Trainer with username '%s' not found", username)
                ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AssignedTrainerDTO> getUnassignedTrainers(String traineeUsername) {
        logger.debug("Getting unassigned trainers for trainee username");

        validator.validateUsername(traineeUsername);

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Trainee with username '%s' not found", traineeUsername)
                ));

        Set<Trainer> assignedTrainers = trainee.getTrainers();
        List<Trainer> allTrainers = trainerRepository.findAll();

        List<Trainer> unassignedTrainers = allTrainers.stream()
                .filter(trainer -> !assignedTrainers.contains(trainer))
                .toList();

        logger.info("Found {} unassigned trainers for trainee '{}'",
                unassignedTrainers.size(), trainee.getUser().getUsername());
        return unassignedTrainers.stream()
                .map(trainerMapper::toAssignedDto)
                .toList();
    }
}

