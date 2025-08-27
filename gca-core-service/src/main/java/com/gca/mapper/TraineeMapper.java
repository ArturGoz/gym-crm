package com.gca.mapper;

import com.gca.dto.trainee.AssignedTraineeDTO;
import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.model.Trainee;
import com.gca.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "springlazy",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.SETTER,
        uses = {TrainerMapper.class})
public interface TraineeMapper {
    Trainee toEntity(TraineeCreateDTO request);

    Trainee toEntity(TraineeUpdateRequestDTO request);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainers")
    TraineeGetDTO toGetDto(Trainee trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    AssignedTraineeDTO toAssignedDto(Trainee trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainers")
    TraineeUpdateResponseDTO toUpdateResponse(Trainee trainee);

    default User fillUserFields(User oldUser, TraineeUpdateRequestDTO request) {
        return oldUser.toBuilder()
                .username(request.getUsername())
                .isActive(request.getIsActive())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    default Trainee fillTraineeFields(Trainee oldTrainee, User updatedUser, TraineeUpdateRequestDTO request) {
        return oldTrainee.toBuilder()
                .user(updatedUser)
                .dateOfBirth(ofNullable(request.getDateOfBirth())
                        .orElse(oldTrainee.getDateOfBirth()))
                .address(ofNullable(request.getAddress())
                        .orElse(oldTrainee.getAddress()))
                .build();
    }
}
