package com.gca.integration.web;

import com.gca.dto.trainer.TrainerWorkloadDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
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
    }

    @Test
    void processTrainerWorkloadRequest_success() {
        ResponseEntity<Void> expected = ResponseEntity.ok().build();

        when(restTemplate.postForEntity(anyString(), eq(request), eq(Void.class)))
                .thenReturn(expected);

        ResponseEntity<Void> actual = sut.processTrainerWorkloadRequest(request);

        assertThat(actual).isEqualTo(expected);
        verify(restTemplate).postForEntity(anyString(), eq(request), eq(Void.class));
    }

    @Test
    void fallback_triggered() {
        RuntimeException ex = new RuntimeException("Service down");

        ResponseEntity<Void> actual = ReflectionTestUtils.invokeMethod(sut,
                "fallback", request, ex);

        assertNotNull(actual);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}