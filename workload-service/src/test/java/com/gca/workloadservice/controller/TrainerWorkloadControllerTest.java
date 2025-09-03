package com.gca.workloadservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static com.gca.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.ADD;
import static com.gca.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.DELETE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    private final String trainersApi = String.format("%s/%s", ApiConstant.BASE_PATH, "trainers");

    @Mock
    private TrainerWorkloadService service;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        TrainerWorkloadController controller = new TrainerWorkloadController(service);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void addOrDeleteTrainerWorkload_shouldCallAdd_whenActionIsAdd() throws Exception {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest(ADD);

        mockMvc.perform(post(trainersApi + "/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).addTrainingWorkload(request);
        verify(service, never()).deleteTrainingWorkload(anyString());
    }

    @Test
    void addOrDeleteTrainerWorkload_shouldCallDelete_whenActionIsDelete() throws Exception {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest(DELETE);

        mockMvc.perform(post(trainersApi + "/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).deleteTrainingWorkload("taras.shevchenko");
        verify(service, never()).addTrainingWorkload(any());
    }

    private TrainerWorkloadRequest buildTrainerWorkloadRequest(TrainerWorkloadRequest.ActionTypeEnum actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("taras.shevchenko");
        request.setTrainerFirstName("taras");
        request.setTrainerLastName("shevchenko");
        request.setTrainingDate(LocalDate.of(3333, 1, 1));
        request.setIsActive(true);
        request.setActionType(actionType);

        return request;
    }
}