package com.gca.workloadservice.mapper;

import com.gca.workloadservice.dto.TrainerWorkloadDTO;
import com.gca.workloadservice.model.TrainerWorkload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainerWorkloadMapper {

    @Mapping(source = "trainerUsername", target = "username")
    @Mapping(source = "trainerFirstName", target = "firstName")
    @Mapping(source = "trainerLastName", target = "lastName")
    @Mapping(source = "isActive", target = "isActive")
    TrainerWorkload toEntity(TrainerWorkloadDTO dto);
}
