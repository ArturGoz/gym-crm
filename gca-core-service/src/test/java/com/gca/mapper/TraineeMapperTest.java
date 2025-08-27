package com.gca.mapper;

import com.gca.utils.GymTestProvider;
import com.gca.dto.trainee.TraineeCreateDTO;
import com.gca.dto.trainee.AssignedTraineeDTO;
import com.gca.dto.trainee.TraineeUpdateRequestDTO;
import com.gca.model.Trainee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TraineeMapperTest {
    TraineeMapper mapper = new TraineeMapperImpl();

    @Test
    void testToEntity_fromCreateRequest() {
        TraineeCreateDTO request = TraineeCreateDTO.builder()
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();

        Trainee entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(LocalDate.of(2000, 1, 1), entity.getDateOfBirth());
        assertEquals("Kyiv", entity.getAddress());
    }

    @Test
    void testToEntity_fromUpdateRequest() {
        TraineeUpdateRequestDTO request = GymTestProvider.createTraineeUpdateRequestDTO();

        Trainee entity = mapper.toEntity(request);

        assertEquals(request.getDateOfBirth(), entity.getDateOfBirth());
        assertEquals(request.getAddress(), entity.getAddress());
    }

    @Test
    void testToGetDto() {
        Trainee expected = GymTestProvider.constructTrainee();

        AssignedTraineeDTO response = mapper.toAssignedDto(expected);

        assertNotNull(response);
        assertEquals(expected.getUser().getUsername(), response.getUsername());
        assertEquals(expected.getUser().getFirstName(), response.getFirstName());
        assertEquals(expected.getUser().getLastName(), response.getLastName());
    }
}