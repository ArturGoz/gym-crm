package com.gca.mapper.rest;

import com.gca.dto.trainer.AssignedTrainerDTO;
import com.gca.dto.trainer.TrainerCreateDTO;
import com.gca.dto.trainer.TrainerGetDTO;
import com.gca.dto.trainer.TrainerUpdateRequestDTO;
import com.gca.dto.trainer.TrainerUpdateResponseDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.openapi.model.AssignedTrainerResponse;
import com.gca.openapi.model.TrainerCreateRequest;
import com.gca.openapi.model.TrainerCreateResponse;
import com.gca.openapi.model.TrainerGetResponse;
import com.gca.openapi.model.TrainerUpdateRequest;
import com.gca.openapi.model.TrainerUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RestTraineeMapper.class}
)
public interface RestTrainerMapper {

    AssignedTrainerResponse toRest(AssignedTrainerDTO dto);

    TrainerCreateDTO toDto(TrainerCreateRequest request);

    TrainerCreateResponse toRest(UserCredentialsDTO dto);

    @Mapping(source = "username", target = "username")
    TrainerUpdateRequestDTO toDto(String username, TrainerUpdateRequest request);

    TrainerUpdateResponse toRest(TrainerUpdateResponseDTO dto);

    TrainerGetResponse toRest(TrainerGetDTO dto);
}
