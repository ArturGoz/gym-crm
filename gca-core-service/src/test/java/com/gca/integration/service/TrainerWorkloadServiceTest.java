package com.gca.integration.service;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.integration.sender.WorkloadSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.gca.dto.trainer.ActionType.ADD;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private WorkloadSender sender;

    @InjectMocks
    private TrainerWorkloadService service;

    @Test
    void shouldDelegateToWorkloadSender() {
        TrainerWorkloadDTO dto = buildTrainerWorkloadDTO();
        service.notifyWorkloadService(dto);
        verify(sender).processTrainerWorkloadRequest(dto);
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
