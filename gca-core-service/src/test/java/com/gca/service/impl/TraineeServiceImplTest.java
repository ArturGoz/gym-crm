package com.gca.service.impl;

import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeTrainersUpdateDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.mapper.TraineeMapper;
import com.gca.mapper.TrainerMapper;
import com.gca.mapper.UserMapper;
import com.gca.model.Trainee;
import com.gca.model.Trainer;
import com.gca.model.User;
import com.gca.repository.TraineeRepository;
import com.gca.repository.TrainerRepository;
import com.gca.service.UserService;
import com.gca.service.common.CoreValidator;
import com.gca.service.common.UserProfileService;
import com.gca.utils.GymTestProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository repository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeMapper mapper;

    @Mock
    private CoreValidator validator;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TraineeServiceImpl service;

    @Test
    void createTrainee_success() {
        TraineeCreateDTO request = GymTestProvider.createTraineeCreateDTO();
        Trainee trainee = GymTestProvider.constructTrainee();
        Trainee traineeWithCreds = GymTestProvider.constructTrainee();
        UserCredentialsDTO expected = GymTestProvider.constructUserCreateDTO();

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(trainee.getUser());
        when(userProfileService.encryptPassword(trainee.getUser().getPassword())).thenReturn(expected.getPassword());
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(repository.save(any(Trainee.class))).thenReturn(traineeWithCreds);
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        UserCredentialsDTO actual = service.createTrainee(request);

        assertEquals(expected, actual);
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getUsername(), actual.getUsername());
        verify(mapper).toEntity(request);
        verify(repository).save(any(Trainee.class));
        verify(userMapper).toResponse(any(User.class));
    }

    @Test
    void updateTrainee_success() {
        TraineeUpdateRequestDTO updateRequest = GymTestProvider.createTraineeUpdateRequestDTO();
        User filledUser = GymTestProvider.constructUser();
        Trainee existing = GymTestProvider.constructTrainee();

        Trainee filledExistingTrainee = existing.toBuilder()
                .user(filledUser)
                .address(updateRequest.getAddress())
                .dateOfBirth(updateRequest.getDateOfBirth())
                .build();
        Trainee updated = filledExistingTrainee.toBuilder()
                .id(existing.getId())
                .build();
        TraineeUpdateResponseDTO expected =
                GymTestProvider.createTraineeUpdateResponse(updated);

        when(repository.findByUserUsername(updateRequest.getUsername())).thenReturn(Optional.of(existing));
        when(mapper.fillUserFields(existing.getUser(), updateRequest)).thenReturn(filledUser);
        when(mapper.fillTraineeFields(existing, filledUser, updateRequest)).thenReturn(filledExistingTrainee);
        when(repository.save(filledExistingTrainee)).thenReturn(updated);
        when(mapper.toUpdateResponse(updated)).thenReturn(expected);

        TraineeUpdateResponseDTO actual = service.updateTrainee(updateRequest);

        assertEquals(expected, actual);
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getIsActive(), actual.getIsActive());

        verify(repository).save(existing);
        verify(mapper).toUpdateResponse(updated);
    }

    @Test
    void updateTrainee_notFound_throwsException() {
        TraineeUpdateRequestDTO updateRequest = GymTestProvider.createTraineeUpdateRequestDTO();

        when(repository.findByUserUsername(updateRequest.getUsername())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateTrainee(updateRequest));
        assertEquals("Invalid trainee username: arnold.schwarzenegger", ex.getMessage());
    }

    @Test
    void getTraineeByUsername_success() {
        String username = "john_doe";
        Trainee mockTrainee = GymTestProvider.constructTrainee();
        TraineeGetDTO expectedResponse = GymTestProvider.createTraineeGetDTO();

        when(repository.findByUserUsername(username)).thenReturn(ofNullable(mockTrainee));
        when(mapper.toGetDto(mockTrainee)).thenReturn(expectedResponse);

        TraineeGetDTO actualResponse = service.getTraineeByUsername(username);

        assertEquals(expectedResponse, actualResponse);
        verify(validator).validateUsername(username);
        verify(repository).findByUserUsername(username);
        verify(mapper).toGetDto(mockTrainee);
    }

    @Test
    void deleteTraineeByUsername_shouldThrow_whenNotFound() {
        String username = "ghost";

        when(repository.findByUserUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> service.deleteTraineeByUsername(username));

        assertEquals(("Trainee with username 'ghost' not found"), ex.getMessage());
        verify(repository).findByUserUsername(username);
    }

    @Test
    void updateTraineeTrainers_shouldUpdateAndReturnResponse() {
        String username = "john.doe";
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        TraineeTrainersUpdateDTO request = new TraineeTrainersUpdateDTO(username, trainerUsernames);

        Trainee trainee = GymTestProvider.constructTrainee();
        Trainer trainer1 = GymTestProvider.constructTrainer(1L, "trainer1");
        Trainer trainer2 = GymTestProvider.constructTrainer(2L, "trainer2");
        trainee.setTrainers(Set.of(trainer1, trainer2));
        AssignedTrainerDTO dto1 = GymTestProvider.createAssignedTrainerDTO("trainer1");
        AssignedTrainerDTO dto2 = GymTestProvider.createAssignedTrainerDTO("trainer2");


        when(repository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.findByUserUsername("trainer2")).thenReturn(Optional.of(trainer2));

        when(repository.save(any(Trainee.class))).thenReturn(trainee);

        when(trainerMapper.toAssignedDto(trainer1)).thenReturn(dto1);
        when(trainerMapper.toAssignedDto(trainer2)).thenReturn(dto2);

        List<AssignedTrainerDTO> actual = service.updateTraineeTrainers(request);

        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(dto1, dto2)));
        verify(repository).findByUserUsername(username);
        verify(trainerRepository).findByUserUsername("trainer1");
        verify(trainerRepository).findByUserUsername("trainer2");
        verify(repository).save(any(Trainee.class));
        verify(trainerMapper).toAssignedDto(trainer1);
        verify(trainerMapper).toAssignedDto(trainer2);
    }
}