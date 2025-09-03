package com.gca.controller;

import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.LoginChangeRequest;
import com.gca.openapi.model.LoginRequest;
import com.gca.utils.GymTestProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.gca.controller.ApiConstant.BASE_PATH;
import static com.gca.utils.JsonUtils.asJsonString;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private final String authApi = format("%s/%s", BASE_PATH, "auth");

    @MockitoBean
    private TrainingAppFacade facade;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_shouldReturnOk() throws Exception {
        LoginRequest request = GymTestProvider.createLoginRequest();

        mockMvc.perform(post(format("%s/%s", authApi, "/login"))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(facade).login(any(LoginRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void changeLogin_shouldReturnOk() throws Exception {
        LoginChangeRequest request = GymTestProvider.createLoginChangeRequest();

        mockMvc.perform(put(format("%s/%s", authApi, "/login"))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());

        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void logout_shouldReturnOk() throws Exception {
        mockMvc.perform(post(format("%s/%s", authApi, "/logout")))
                .andExpect(status().isOk());

        verify(facade).logout(any(HttpServletResponse.class));
    }

    @Test
    void refreshToken_shouldReturnOk() throws Exception {
        mockMvc.perform(post(format("%s/%s", authApi, "/refresh")))
                .andExpect(status().isOk());

        verify(facade).refreshToken(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
