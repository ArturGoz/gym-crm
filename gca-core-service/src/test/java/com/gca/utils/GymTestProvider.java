package com.gca.utils;

import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.dto.trainee.AssignedTraineeDTO;
import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerCreateDTO;
import com.gca.dto.trainer.TrainerGetDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.Training;
import com.gca.model.TrainingType;
import com.gca.model.User;
import com.gca.openapi.model.AssignedTraineeResponse;
import com.gca.openapi.model.AssignedTrainerResponse;
import com.gca.openapi.model.LoginChangeRequest;
import com.gca.openapi.model.LoginRequest;
import com.gca.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gca.openapi.model.TraineeCreateRequest;
import com.gca.openapi.model.TraineeCreateResponse;
import com.gca.openapi.model.TraineeGetResponse;
import com.gca.openapi.model.TraineeUpdateRequest;
import com.gca.openapi.model.TraineeUpdateResponse;
import com.gca.openapi.model.TrainerCreateRequest;
import com.gca.openapi.model.TrainerCreateResponse;
import com.gca.openapi.model.TrainerGetResponse;
import com.gca.openapi.model.TrainerUpdateRequest;
import com.gca.openapi.model.TrainerUpdateResponse;
import com.gca.openapi.model.TrainingCreateRequest;
import com.gca.openapi.model.TrainingGetResponse;
import com.gca.openapi.model.TrainingTypeResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class GymTestProvider {

    public static TraineeGetResponse createTraineeGetResponse() {
        TraineeGetResponse response = new TraineeGetResponse();
        response.setFirstName("Arnold");
        response.setLastName("Schwarzenegger");
        response.setIsActive(true);
        response.setAddress("Kyiv, Khreschatyk 10");
        response.setDateOfBirth(LocalDate.of(1990, 1, 1));
        response.setTrainers(List.of(createAssignedTrainerResponse()));

        return response;
    }

    public static TraineeCreateResponse createTraineeCreateResponse() {
        TraineeCreateResponse traineeCreateResponse = new TraineeCreateResponse();
        traineeCreateResponse.setUsername("arnold.schwarzenegger");
        traineeCreateResponse.setPassword("password");

        return traineeCreateResponse;
    }

    public static TraineeCreateRequest createTraineeCreateRequest() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName("Arnold");
        request.setLastName("Schwarzenegger");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("Kyiv, Shevchenka 1");

        return request;
    }

    public static TrainerUpdateResponseDTO createTrainerUpdateResponseDTO() {
        return TrainerUpdateResponseDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .isActive(true)
                .trainees(List.of(createAssignedTraineeDTO()))
                .specialization("Yoga")
                .build();
    }

    public static TrainerUpdateResponseDTO createTrainerUpdateResponseDTO(Trainer trainer) {
        return TrainerUpdateResponseDTO.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .isActive(trainer.getUser().getIsActive())
                .trainees(List.of(createAssignedTraineeDTO()))
                .specialization(trainer.getSpecialization().getName())
                .build();
    }

    public static TraineeUpdateResponseDTO createTraineeUpdateResponse() {
        return TraineeUpdateResponseDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .isActive(true)
                .trainers(List.of(createAssignedTrainerDTO()))
                .address("Address")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    public static TraineeUpdateResponseDTO createTraineeUpdateResponse(Trainee trainee) {
        return TraineeUpdateResponseDTO.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUser().getUsername())
                .isActive(trainee.getUser().getIsActive())
                .trainers(List.of(createAssignedTrainerDTO()))
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .build();
    }

    public static TrainerCreateDTO createTrainerCreateDTO() {
        return TrainerCreateDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .specialization("Boxing")
                .build();
    }

    public static TrainerUpdateRequestDTO createTrainerUpdateRequestDTO() {
        return TrainerUpdateRequestDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .isActive(true)
                .username("arnold.schwarzenegger")
                .specialization("Crossfit")
                .build();
    }

    public static Trainer constructTrainer() {
        return Trainer.builder()
                .id(2L)
                .specialization(TrainingType.builder().name("Yoga").build())
                .user(User.builder().id(10L).build())
                .build();
    }

    public static Trainer constructTrainer(Long id) {
        return constructTrainer().toBuilder()
                .id(id)
                .user(User.builder().id(id).build())
                .build();
    }

    public static Trainer constructTrainer(Long id, String username) {
        return constructTrainer(id).toBuilder()
                .user(User.builder().username(username).build())
                .build();
    }

    public static Trainer constructInactiveTrainer() {
        return Trainer.builder()
                .id(2L)
                .specialization(TrainingType.builder().name("Pilates").build())
                .user(User.builder().id(2L).build())
                .build();
    }

    public static AssignedTrainerDTO createAssignedTrainerDTO() {
        return AssignedTrainerDTO.builder()
                .firstName("Ronnie")
                .lastName("Coleman")
                .username("ronnie.coleman")
                .specialization("Yoga")
                .build();
    }

    public static AssignedTrainerDTO createAssignedTrainerDTO(String trainerFirstName) {
        return createAssignedTrainerDTO().toBuilder().firstName(trainerFirstName).build();
    }

    public static AssignedTrainerResponse createAssignedTrainerResponse() {
        AssignedTrainerResponse response = new AssignedTrainerResponse();
        response.setFirstName("Ronnie");
        response.setLastName("Coleman");
        response.setUsername("ronnie.coleman");
        response.setSpecialization("Yoga");
        return response;
    }

    public static AssignedTrainerResponse createAssignedTrainerResponse(String trainerFirstName) {
        return createAssignedTrainerResponse().firstName(trainerFirstName);
    }

    public static TraineeCreateDTO createTraineeCreateDTO() {
        return TraineeCreateDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv, Shevchenka 1")
                .build();
    }

    public static TraineeUpdateRequestDTO createTraineeUpdateRequestDTO() {
        return TraineeUpdateRequestDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Kyiv, Khreschatyk 10")
                .build();
    }

    public static Trainee constructTrainee() {
        return Trainee.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .address("Kyiv, Shevchenka 1")
                .trainers(Set.of(constructTrainer(2L), constructTrainer(3L)))
                .build();
    }

    public static UserCredentialsDTO constructUserCreateDTO() {
        return UserCredentialsDTO.builder()
                .username("arnold.schwarzenegger")
                .password("password")
                .build();
    }

    public static AssignedTraineeDTO createAssignedTraineeDTO() {
        return AssignedTraineeDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger1")
                .build();
    }

    public static TrainingCreateDTO createTrainingCreateRequestDTO() {
        return TrainingCreateDTO.builder()
                .trainerUsername("arnold.schwarzenegger")
                .traineeUsername("arnold.schwarzenegger1")
                .trainingName("Strength")
                .trainingDate(LocalDate.of(2025, 7, 4))
                .duration(60L)
                .build();
    }

    public static Training constructTrainingWithoutId() {
        return Training.builder()
                .date(LocalDate.of(2025, 7, 4))
                .duration(60L)
                .name("Cardio Session")
                .build();
    }

    public static Training constructTraining() {
        return Training.builder()
                .id(1L)
                .date(LocalDate.of(2025, 7, 4))
                .duration(60L)
                .name("Cardio Session")
                .build();
    }

    public static TrainingDTO createTrainingDTO() {
        return TrainingDTO.builder()
                .traineeName("Arnold")
                .trainerName("Ronnie")
                .trainingName("Strength")
                .trainingDate(LocalDate.of(2025, 7, 4))
                .trainingDuration(60L)
                .build();
    }

    public static UserCreateDTO createUserCreateRequest() {
        return UserCreateDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .build();
    }

    public static User constructUser() {
        return User.builder()
                .id(1L)
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("arnold.schwarzenegger")
                .password("securePass123")
                .isActive(true)
                .build();
    }

    public static TrainingType createTrainingTypeStrength() {
        return createTrainingType(1L, "Strength");
    }

    public static TrainingType createTrainingTypeYoga() {
        return createTrainingType(2L, "Yoga");
    }

    public static TrainingType createTrainingType(Long id, String name) {
        return TrainingType.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static TrainingTraineeCriteriaFilter buildTraineeCriteriaFilter() {
        return TrainingTraineeCriteriaFilter.builder()
                .traineeUsername("arnold.schwarzenegger")
                .fromDate(LocalDate.of(2025, 7, 1))
                .toDate(LocalDate.of(2025, 7, 31))
                .trainerName("ronnie.coleman")
                .trainingTypeName("Yoga")
                .build();
    }

    public static TrainingTrainerCriteriaFilter buildTrainerCriteriaFilter() {
        return TrainingTrainerCriteriaFilter.builder()
                .trainerUsername("arnold.schwarzenegger")
                .fromDate(LocalDate.of(2025, 7, 1))
                .toDate(LocalDate.of(2025, 7, 31))
                .traineeName("arnold.schwarzenegger")
                .build();
    }

    public static TraineeUpdateRequest createTraineeUpdateRequest() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setFirstName("Arnold");
        request.setLastName("Schwarzenegger");
        request.setIsActive(true);
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("Kyiv, Khreschatyk 10");

        return request;
    }

    public static TraineeUpdateResponse createTraineeUpdateRestResponse() {
        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setFirstName("Arnold");
        response.setLastName("Schwarzenegger");
        response.setUsername("arnold.schwarzenegger");
        response.setIsActive(true);
        response.setTrainers(List.of(createAssignedTrainerResponse()));
        response.setAddress("Address");
        response.setDateOfBirth(LocalDate.of(1990, 1, 1));

        return response;
    }

    public static TraineeGetDTO createTraineeGetDTO() {
        return TraineeGetDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .isActive(true)
                .address("Kyiv, Khreschatyk 10")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .trainers(List.of(createAssignedTrainerDTO()))
                .build();
    }

    public static AssignedTrainerResponse createTrainerResponse(String trainerFirstName) {
        return createAssignedTrainerResponse().firstName(trainerFirstName);
    }

    public static TraineeAssignedTrainersUpdateRequest createTraineeAssignedTrainersUpdateRequest() {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(List.of("trainer1", "trainer2"));

        return request;
    }

    public static TrainerCreateRequest createTrainerCreateRequest() {
        TrainerCreateRequest request = new TrainerCreateRequest();
        request.setFirstName("Arnold");
        request.setLastName("Schwarzenegger");
        request.setSpecialization("Boxing");

        return request;
    }

    public static TrainerCreateResponse createTrainerCreateResponse() {
        TrainerCreateResponse response = new TrainerCreateResponse();
        response.setUsername("arnold.schwarzenegger");
        response.setPassword("password");

        return response;
    }

    public static TrainerUpdateRequest createTrainerUpdateRequest() {
        TrainerUpdateRequest request = new TrainerUpdateRequest();
        request.setFirstName("Arnold");
        request.setLastName("Schwarzenegger");
        request.setIsActive(true);
        request.setSpecialization("Crossfit");

        return request;
    }

    public static TrainerUpdateResponse createTrainerUpdateResponse() {
        TrainerUpdateResponse response = new TrainerUpdateResponse();
        response.setFirstName("Arnold");
        response.setLastName("Schwarzenegger");
        response.setUsername("arnold.schwarzenegger");
        response.setIsActive(true);
        response.setTrainees(List.of(createAssignedTraineeResponse()));
        response.setSpecialization("Yoga");

        return response;
    }

    public static TrainerGetDTO createTrainerGetDTO() {
        return TrainerGetDTO.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .isActive(true)
                .specialization("Crossfit")
                .trainees(List.of(createAssignedTraineeDTO()))
                .build();
    }

    public static TrainerGetResponse createTrainerGetResponse() {
        TrainerGetResponse response = new TrainerGetResponse();
        response.setFirstName("Arnold");
        response.setLastName("Schwarzenegger");
        response.setIsActive(true);
        response.setSpecialization("Crossfit");
        response.setTrainees(List.of(createAssignedTraineeResponse()));

        return response;
    }

    public static AssignedTraineeResponse createAssignedTraineeResponse() {
        AssignedTraineeResponse response = new AssignedTraineeResponse();
        response.setFirstName("Arnold");
        response.setLastName("Schwarzenegger");
        response.setUsername("arnold.schwarzenegger");

        return response;
    }

    public static TrainingCreateRequest createTrainingCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainerUsername("arnold.schwarzenegger");
        request.setTraineeUsername("arnold.schwarzenegger1");
        request.setTrainingName("Strength");
        request.setTrainingDate(LocalDate.of(2025, 7, 4));
        request.setDuration(60);

        return request;
    }

    public static TrainingTypeResponse createTrainingTypeResponse(Integer trainingTypeId, String trainingTypeName) {
        TrainingTypeResponse response = new TrainingTypeResponse();
        response.setName(trainingTypeName);
        response.setId(trainingTypeId);

        return response;
    }

    public static TrainingGetResponse createTrainingGetResponse() {
        TrainingGetResponse response = new TrainingGetResponse();
        response.setTraineeName("arnold.schwarzenegger");
        response.setTrainerName("arnold.schwarzenegger1");
        response.setTrainingName("Strength");
        response.setTrainingDate(LocalDate.of(2025, 7, 4));
        response.setTrainingDuration(60);

        return response;
    }

    public static TrainingTrainerCriteriaFilter createTrainingTrainerCriteriaFilter() {
        return TrainingTrainerCriteriaFilter.builder()
                .traineeName("arnold.schwarzenegger")
                .trainerUsername("arnold.schwarzenegger1")
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .build();
    }

    public static TrainingTraineeCriteriaFilter createTrainingTraineeCriteriaFilter() {
        return TrainingTraineeCriteriaFilter.builder()
                .trainerName("arnold.schwarzenegger")
                .traineeUsername("arnold.schwarzenegger1")
                .trainingTypeName("Strength")
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .build();
    }

    public static LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("arnold.schwarzenegger");
        request.setPassword("************");

        return request;
    }

    public static LoginChangeRequest createLoginChangeRequest() {
        LoginChangeRequest request = new LoginChangeRequest();
        request.setUsername("arnold.schwarzenegger");
        request.setOldPassword("*******");
        request.setNewPassword("******************");

        return request;
    }
}
