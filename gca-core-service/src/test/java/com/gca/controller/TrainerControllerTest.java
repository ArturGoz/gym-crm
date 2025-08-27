package com.gca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.dto.filter.TrainingTrainerCriteriaFilter;
import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.ActivationStatusRequest;
import com.gca.openapi.model.TrainerCreateRequest;
import com.gca.openapi.model.TrainerCreateResponse;
import com.gca.openapi.model.TrainerGetResponse;
import com.gca.openapi.model.TrainerUpdateRequest;
import com.gca.openapi.model.TrainerUpdateResponse;
import com.gca.openapi.model.TrainingGetResponse;
import com.gca.utils.GymTestProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.gca.controller.ApiConstant.BASE_PATH;
import static com.gca.utils.JsonUtils.asJsonString;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TrainerControllerTest {

    private final String trainerApi = format("%s/trainers", BASE_PATH);
    private final String trainerRegisterApi = format("%s/register", trainerApi);
    private final String username = "ricardo.milos";

    @Mock
    private TrainingAppFacade facade;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TrainerController controller = new TrainerController(facade);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void registerTrainer_returnsOkResponse() throws Exception {
        TrainerCreateRequest request = GymTestProvider.createTrainerCreateRequest();
        TrainerCreateResponse response = GymTestProvider.createTrainerCreateResponse();

        when(facade.createTrainer(any())).thenReturn(response);

        mockMvc.perform(post(trainerRegisterApi)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));

        verify(facade).createTrainer(any());
    }

    @Test
    void getTrainerByUsername_returnsTrainer() throws Exception {
        TrainerGetResponse response = GymTestProvider.createTrainerGetResponse();

        when(facade.getTrainerByUsername(username)).thenReturn(response);

        mockMvc.perform(get(format("%s/%s", trainerApi, username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()));

        verify(facade).getTrainerByUsername(username);
    }

    @Test
    void updateTrainer_returnsUpdatedTrainer() throws Exception {
        TrainerUpdateRequest request = GymTestProvider.createTrainerUpdateRequest();
        TrainerUpdateResponse response = GymTestProvider.createTrainerUpdateResponse();

        when(facade.updateTrainer(eq(username), any())).thenReturn(response);

        mockMvc.perform(put(format("%s/%s", trainerApi, username))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainees.length()").value(response.getTrainees().size()));

        verify(facade).updateTrainer(eq(username), any());
    }

    @Test
    void getTrainerTrainings_returnsFilteredTrainings() throws Exception {
        TrainingTrainerCriteriaFilter filter = GymTestProvider.createTrainingTrainerCriteriaFilter();
        List<TrainingGetResponse> responses = List.of(GymTestProvider.createTrainingGetResponse());

        when(facade.findFilteredTrainings(filter)).thenReturn(responses);

        mockMvc.perform(get(format("%s/%s/trainings", trainerApi, "arnold.schwarzenegger1"))
                        .param("username", filter.getTrainerUsername())
                        .param("periodFrom", filter.getFromDate().toString())
                        .param("periodTo", filter.getToDate().toString())
                        .param("traineeName", filter.getTraineeName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responses.size()))
                .andExpect(jsonPath("$[0].traineeName").value(responses.get(0).getTraineeName()))
                .andExpect(jsonPath("$[0].trainerName").value(responses.get(0).getTrainerName()))
                .andExpect(jsonPath("$[0].trainingName").value(responses.get(0).getTrainingName()))
                .andExpect(jsonPath("$[0].trainingDate").value(responses.get(0).getTrainingDate().toString()))
                .andExpect(jsonPath("$[0].trainingDuration").value(responses.get(0).getTrainingDuration()));
    }

    @Test
    void updateTrainerActivationStatus_shouldReturnOk() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest(true);

        mockMvc.perform(patch(format("%s/%s/change-activation-status", trainerApi, username))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(facade).toggleUserActiveStatus(username, request.getIsActive());
    }
}