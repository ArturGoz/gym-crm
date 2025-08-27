package com.gca.actuator.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationMetricsTest {

    @Mock
    private Counter successfulRegistrations;

    @Mock
    private MeterRegistry meterRegistry;

    private UserRegistrationMetrics userRegistrationMetrics;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter(
                "user.registration.success",
                "description", "The number of successful user registrations"
        )).thenReturn(successfulRegistrations);
        userRegistrationMetrics = new UserRegistrationMetrics(meterRegistry);
    }

    @Test
    void incrementSuccessCount_shouldIncrementCounter() {
        userRegistrationMetrics.incrementSuccessCount();
        verify(successfulRegistrations).increment();
    }
}