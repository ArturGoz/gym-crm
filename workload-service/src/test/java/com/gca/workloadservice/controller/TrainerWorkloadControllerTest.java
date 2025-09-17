package com.gca.workloadservice.controller;

import com.gca.workloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TrainerWorkloadControllerTest {

    private final String trainersApi = String.format("%s/%s", ApiConstant.BASE_PATH, "trainers");

    @MockitoBean
    private TrainerWorkloadService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTrainerWorkload_ReturnsWorkloadDurationSummary() throws Exception {
        String username = "ronnie.coleman";
        int year = 2025;
        int month = 3;
        long expectedWorkload = 300L;

        when(service.getTrainerWorkloadDurationSummary(username, year, month))
                .thenReturn(expectedWorkload);

        mockMvc.perform(get(trainersApi + "/workload/{username}", username)
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedWorkload)));

        verify(service).getTrainerWorkloadDurationSummary(username, year, month);
    }
}