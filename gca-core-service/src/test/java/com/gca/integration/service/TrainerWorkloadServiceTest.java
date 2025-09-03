package com.gca.integration.service;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.exception.ServiceException;
import com.gca.integration.web.WorkloadConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static com.gca.dto.trainer.ActionType.ADD;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private WorkloadConnector connector;

    @InjectMocks
    private TrainerWorkloadService service;

    @Test
    void addOrDeleteTrainerWorkload_successfulResponse_doesNotThrow() {
        TrainerWorkloadDTO request = buildTrainerWorkloadDTO();

        when(connector.processTrainerWorkloadRequest(request))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        assertThatCode(() -> service.addOrDeleteTrainerWorkload(request))
                .doesNotThrowAnyException();

        verify(connector).processTrainerWorkloadRequest(request);
    }

    @Test
    void addOrDeleteTrainerWorkload_failedResponse_throwsServiceException() {
        TrainerWorkloadDTO request = buildTrainerWorkloadDTO();

        when(connector.processTrainerWorkloadRequest(request))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> service.addOrDeleteTrainerWorkload(request))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Failed to process trainer workload request");

        verify(connector).processTrainerWorkloadRequest(request);
    }

    private TrainerWorkloadDTO buildTrainerWorkloadDTO() {
        return TrainerWorkloadDTO.builder()
                .trainerUsername("john.cena")
                .trainerFirstName("John")
                .trainerLastName("Cena")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60)
                .isActive(true)
                .actionType(ADD)
                .build();
    }
}