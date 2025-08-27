package com.gca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.dto.filter.TrainingTraineeCriteriaFilter;
import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.ActivationStatusRequest;
import com.gca.openapi.model.AssignedTrainerResponse;
import com.gca.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gca.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gca.openapi.model.TraineeCreateRequest;
import com.gca.openapi.model.TraineeCreateResponse;
import com.gca.openapi.model.TraineeGetResponse;
import com.gca.openapi.model.TraineeUpdateRequest;
import com.gca.openapi.model.TraineeUpdateResponse;
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
import static com.gca.utils.JsonUtils.readJson;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TraineeControllerTest {

    private final String traineeApi = format("%s/trainees", BASE_PATH);
    private final String traineeRegisterApi = format("%s/register", traineeApi);
    private final String username = "arnold.schwarzenegger";

    @Mock
    private TrainingAppFacade facade;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TraineeController controller = new TraineeController(facade);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    public void registerTrainee_returnsOkResponse() throws Exception {
        TraineeCreateRequest request = GymTestProvider.createTraineeCreateRequest();
        TraineeCreateResponse response = GymTestProvider.createTraineeCreateResponse();

        when(facade.createTrainee(any())).thenReturn(response);

        mockMvc.perform(post(traineeRegisterApi)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));

        verify(facade).createTrainee(any());
    }

    @Test
    void getTraineeByUsername_returnsTrainee() throws Exception {
        TraineeGetResponse response = GymTestProvider.createTraineeGetResponse();

        when(facade.getTraineeByUsername(username)).thenReturn(response);

        mockMvc.perform(get(format("%s/%s", traineeApi, username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()));

        verify(facade).getTraineeByUsername(username);
    }

    @Test
    void updateTrainee_returnsUpdatedTrainee() throws Exception {
        TraineeUpdateRequest request = GymTestProvider.createTraineeUpdateRequest();
        TraineeUpdateResponse response = GymTestProvider.createTraineeUpdateRestResponse();

        when(facade.updateTrainee(eq(username), any())).thenReturn(response);

        mockMvc.perform(put(format("%s/%s", traineeApi, username))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()));

        verify(facade).updateTrainee(eq(username), any());
    }

    @Test
    void deleteTrainee_returnsOk() throws Exception {
        mockMvc.perform(delete(traineeApi + "/" + username))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(username);
    }

    @Test
    void updateTraineeAssignedTrainers_returnsUpdatedTrainers() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = GymTestProvider.createTraineeAssignedTrainersUpdateRequest();
        TraineeAssignedTrainersUpdateResponse response =
                readJson("trainee-assigned-trainers-update-response.json", TraineeAssignedTrainersUpdateResponse.class);

        when(facade.updateTraineeTrainers(eq(username), any())).thenReturn(response);

        mockMvc.perform(put(format("%s/%s/%s", traineeApi, username, "trainers"))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()));

        verify(facade).updateTraineeTrainers(eq(username), any());
    }

    @Test
    void getAvailableTrainers_returnsTrainerList() throws Exception {
        List<AssignedTrainerResponse> trainers = List.of(GymTestProvider.createAssignedTrainerResponse());

        when(facade.getUnassignedTrainers(username)).thenReturn(trainers);

        mockMvc.perform(get(format("%s/%s/%s", traineeApi, username, "available-trainers")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(trainers.size()))
                .andExpect(jsonPath("$[0].username").value(trainers.get(0).getUsername()));

        verify(facade).getUnassignedTrainers(username);
    }

    @Test
    void getTraineeTrainings_returnsFilteredTrainings() throws Exception {
        TrainingTraineeCriteriaFilter filter = GymTestProvider.createTrainingTraineeCriteriaFilter();
        List<TrainingGetResponse> responses = List.of(GymTestProvider.createTrainingGetResponse());

        when(facade.findFilteredTrainings(filter)).thenReturn(responses);

        mockMvc.perform(get(format("%s/%s/trainings", traineeApi, "arnold.schwarzenegger1"))
                        .param("username", filter.getTraineeUsername())
                        .param("periodFrom", filter.getFromDate().toString())
                        .param("periodTo", filter.getToDate().toString())
                        .param("trainerName", filter.getTrainerName())
                        .param("trainingType", filter.getTrainingTypeName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responses.size()))
                .andExpect(jsonPath("$[0].traineeName").value(responses.get(0).getTraineeName()))
                .andExpect(jsonPath("$[0].trainerName").value(responses.get(0).getTrainerName()))
                .andExpect(jsonPath("$[0].trainingDate").value(responses.get(0).getTrainingDate().toString()))
                .andExpect(jsonPath("$[0].trainingName").value(responses.get(0).getTrainingName()))
                .andExpect(jsonPath("$[0].trainingDuration").value(responses.get(0).getTrainingDuration()));
    }

    @Test
    void updateTraineeActivationStatus_shouldReturnOk() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest(true);

        mockMvc.perform(patch(format("%s/%s/change-activation-status", traineeApi, username))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(facade).toggleUserActiveStatus(username, request.getIsActive());
    }
}