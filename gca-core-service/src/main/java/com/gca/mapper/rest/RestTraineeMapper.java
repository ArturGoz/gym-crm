package com.gca.mapper.rest;

import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.TraineeGetDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.trainee.TraineeUpdateResponseDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.openapi.model.TraineeCreateRequest;
import com.gca.openapi.model.TraineeCreateResponse;
import com.gca.openapi.model.TraineeGetResponse;
import com.gca.openapi.model.TraineeUpdateRequest;
import com.gca.openapi.model.TraineeUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RestTrainerMapper.class}
)
public interface RestTraineeMapper {

    TraineeCreateResponse toRest(UserCredentialsDTO dto);

    TraineeCreateDTO toDto(TraineeCreateRequest request);

    TraineeGetResponse toRest(TraineeGetDTO dto);

    @Mapping(source = "username", target = "username")
    TraineeUpdateRequestDTO toDto(String username, TraineeUpdateRequest request);

    TraineeUpdateResponse toRest(TraineeUpdateResponseDTO dto);
}
