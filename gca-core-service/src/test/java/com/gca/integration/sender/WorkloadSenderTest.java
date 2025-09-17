package com.gca.integration.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.dto.trainer.TrainerWorkloadDTO;
import com.gca.exception.ServiceException;
import com.gca.exception.ServiceUnavailableException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadSenderTest {

    private static final String QUEUE_NAME = "test-queue";
    private static final String TX_ID = "tx-123";

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WorkloadMessageFactory messageFactory;

    @Mock
    private MessageCreator messageCreator;

    @InjectMocks
    private WorkloadSender sender;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(sender, "queueName", QUEUE_NAME);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Transaction-Id", TX_ID);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {
        TrainerWorkloadDTO dto = TrainerWorkloadDTO.builder()
                .trainerUsername("trainer1")
                .build();

        when(messageFactory.createMessage(anyString(), anyString()))
                .thenReturn(messageCreator);
        when(objectMapper.writeValueAsString(dto)).thenReturn("{\"trainerUsername\":\"trainer1\"}");

        sender.processTrainerWorkloadRequest(dto);

        verify(jmsTemplate).send(eq(QUEUE_NAME), eq(messageCreator));
        verify(objectMapper).writeValueAsString(dto);
        verify(messageFactory).createMessage("{\"trainerUsername\":\"trainer1\"}", TX_ID);
    }

    @Test
    void shouldThrowServiceExceptionWhenSerializationFails() throws Exception {
        TrainerWorkloadDTO dto = new TrainerWorkloadDTO();

        when(objectMapper.writeValueAsString(dto))
                .thenThrow(new JsonProcessingException("error") {
                });

        Throwable thrown = catchThrowable(() -> sender.processTrainerWorkloadRequest(dto));

        assertThat(thrown)
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("Failed to serialize workload request");
        verify(jmsTemplate, never()).send(anyString(), any());
    }

    @Test
    void fallbackShouldWrapException() {
        String payload = "{\"trainerUsername\":\"trainer1\"}";
        String txId = "tx-123";
        Throwable cause = new RuntimeException("JMS failure");

        Throwable thrown = catchThrowable(() ->
                ReflectionTestUtils.invokeMethod(sender, "fallback", payload, txId, cause)
        );

        assertThat(thrown)
                .isInstanceOf(ServiceUnavailableException.class)
                .hasMessageContaining("Workload service unavailable");
    }
}
