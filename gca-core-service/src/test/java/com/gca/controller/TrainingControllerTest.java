package com.gca.controller;

import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.TrainingCreateRequest;
import com.gca.openapi.model.TrainingTypeResponse;
import com.gca.utils.GymTestProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.gca.controller.ApiConstant.BASE_PATH;
import static com.gca.utils.JsonUtils.asJsonString;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    private final String trainingsApi = format("%s/trainings", BASE_PATH);
    private final String trainingTypesApi = format("%s/%s", trainingsApi, "types");

    @Mock
    private TrainingAppFacade facade;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TrainingController controller = new TrainingController(facade);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createTraining_returnsOk() throws Exception {
        TrainingCreateRequest request = GymTestProvider.createTrainingCreateRequest();

        mockMvc.perform(post(trainingsApi)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(facade).createTraining(any());
    }

    @Test
    void getTrainingTypes_returnsList() throws Exception {
        List<TrainingTypeResponse> responses = List.of(
                GymTestProvider.createTrainingTypeResponse(1, "Cardio"),
                GymTestProvider.createTrainingTypeResponse(2, "Strength")
        );

        when(facade.getAllTrainingTypes()).thenReturn(responses);

        mockMvc.perform(get(trainingTypesApi))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(responses.size()))
                .andExpect(jsonPath("$[0].id").value(responses.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(responses.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(responses.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(responses.get(1).getName()));

        verify(facade).getAllTrainingTypes();
    }
}