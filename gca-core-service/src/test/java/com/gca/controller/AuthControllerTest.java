package com.gca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.facade.TrainingAppFacade;
import com.gca.openapi.model.LoginChangeRequest;
import com.gca.openapi.model.LoginRequest;
import com.gca.utils.GymTestProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.gca.controller.ApiConstant.BASE_PATH;
import static com.gca.utils.JsonUtils.asJsonString;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private final String authApi = format("%s/%s", BASE_PATH, "auth");

    @Mock
    private TrainingAppFacade facade;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(facade);
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

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
