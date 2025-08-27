package com.gca.mapper;

import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.dto.user.UserCreateDTO;
import com.gca.dto.user.UserCredentialsDTO;
import com.gca.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserCreateDTO request);

    User toEntity(TraineeUpdateRequestDTO user);

    UserCredentialsDTO toResponse(User user);
}
