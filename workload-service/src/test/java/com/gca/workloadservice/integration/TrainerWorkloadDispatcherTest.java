package com.gca.workloadservice.integration;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadDispatcherTest {

    @Mock
    private TrainerWorkloadService service;

    @InjectMocks
    private TrainerWorkloadDispatcher dispatcher;

    @Test
    void dispatchTrainerWorkloadRequest_callsAddTrainingWorkload_whenActionIsAdd() {
        TrainerWorkloadRequest request = buildRequest(TrainerWorkloadRequest.ActionTypeEnum.ADD);

        dispatcher.dispatchTrainerWorkloadRequest(request);

        verify(service).addTrainingWorkload(request);
        verify(service, never()).deleteTrainingWorkload(anyString());
    }

    @Test
    void dispatchTrainerWorkloadRequest_callsDeleteTrainingWorkload_whenActionIsDelete() {
        TrainerWorkloadRequest request = buildRequest(TrainerWorkloadRequest.ActionTypeEnum.DELETE);

        dispatcher.dispatchTrainerWorkloadRequest(request);

        verify(service).deleteTrainingWorkload("taras.shevchenko");
        verify(service, never()).addTrainingWorkload(any());
    }

    private TrainerWorkloadRequest buildRequest(TrainerWorkloadRequest.ActionTypeEnum actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("taras.shevchenko");
        request.setTrainerFirstName("taras");
        request.setTrainerLastName("shevchenko");
        request.setTrainingDate(LocalDate.of(3333, 1, 1));
        request.setIsActive(true);
        request.setActionType(actionType);
        request.setTrainingDuration(2);

        return request;
    }
}
