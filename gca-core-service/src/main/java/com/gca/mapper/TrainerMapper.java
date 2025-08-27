package com.gca.mapper;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerGetDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.model.Trainer;
import com.gca.model.TrainingType;
import com.gca.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "springlazy",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.SETTER,
        uses = {TraineeMapper.class})
public interface TrainerMapper {
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "specialization.name", target = "specialization")
    AssignedTrainerDTO toAssignedDto(Trainer trainer);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "specialization.name", target = "specialization")
    @Mapping(source = "trainees", target = "trainees")
    TrainerUpdateResponseDTO toUpdateResponse(Trainer trainer);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "specialization.name", target = "specialization")
    @Mapping(source = "trainees", target = "trainees")
    TrainerGetDTO toGetDto(Trainer trainer);

    default User fillUserFields(User oldUser, TrainerUpdateRequestDTO request) {
        return oldUser.toBuilder()
                .username(request.getUsername())
                .isActive(request.getIsActive())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
    }

    default Trainer fillTrainerFields(Trainer oldTrainer, User updatedUser, TrainingType specialization) {
        return oldTrainer.toBuilder()
                .user(updatedUser)
                .specialization(specialization)
                .build();
    }
}
