package com.gca.mapper.rest;

import com.gca.dto.training.TrainingCreateDTO;
import com.gca.dto.training.TrainingDTO;
import com.gca.model.TrainingType;
import com.gca.openapi.model.TrainingCreateRequest;
import com.gca.openapi.model.TrainingGetResponse;
import com.gca.openapi.model.TrainingTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestTrainingMapper {
    TrainingCreateDTO toDto(TrainingCreateRequest request);

    TrainingTypeResponse toRest(TrainingType trainingType);

    TrainingGetResponse toRest(TrainingDTO dto);
}
