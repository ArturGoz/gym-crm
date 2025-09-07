package com.gca.integration.web;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.gca.controller.ApiConstant.TRAINER_WORKLOAD_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadConnectorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WorkloadConnector sut;

    private TrainerWorkloadDTO request;

    @BeforeEach
    void setUp() {
        request = new TrainerWorkloadDTO();
        request.setTrainerUsername("john_doe");
    }

    @Test
    void processTrainerWorkloadRequest_success() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        ResponseEntity<Void> expected = ResponseEntity.ok().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TrainerWorkloadDTO> httpEntity = new HttpEntity<>(request, headers);

        when(restTemplate.postForEntity(eq(TRAINER_WORKLOAD_URL), eq(httpEntity), eq(Void.class)))
                .thenReturn(expected);

        ResponseEntity<Void> actual = sut.processTrainerWorkloadRequest(request);

        assertThat(actual).isEqualTo(expected);
        verify(restTemplate).postForEntity(
                eq(TRAINER_WORKLOAD_URL),
                eq(httpEntity),
                eq(Void.class)
        );
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void fallback_triggered() {
        RuntimeException ex = new RuntimeException("Service down");

        ResponseEntity<Void> actual = ReflectionTestUtils.invokeMethod(sut,
                "fallback", request, ex);

        assertThat(actual).isNotNull();
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}