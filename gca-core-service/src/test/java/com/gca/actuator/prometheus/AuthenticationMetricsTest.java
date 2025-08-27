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
class AuthenticationMetricsTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter successfulLogins;

    @Mock
    private Counter failedLogins;

    private AuthenticationMetrics authenticationMetrics;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("auth.login.success.count",
                "description", "Number of successful user logins"))
                .thenReturn(successfulLogins);

        when(meterRegistry.counter("auth.login.failed.count",
                "description", "Number of failed user logins"))
                .thenReturn(failedLogins);

        authenticationMetrics = new AuthenticationMetrics(meterRegistry);
    }

    @Test
    void recordSuccessfulLogin_shouldIncrementSuccessCounter() {
        authenticationMetrics.recordSuccessfulLogin();
        verify(successfulLogins).increment();
    }

    @Test
    void recordFailedLogin_shouldIncrementFailedCounter() {
        authenticationMetrics.recordFailedLogin();
        verify(failedLogins).increment();
    }
}