package com.gca.actuator.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMetrics {

    private final Counter successfulRegistrations;

    public UserRegistrationMetrics(MeterRegistry meterRegistry) {
        this.successfulRegistrations = meterRegistry.counter("user.registration.success",
                "description", "The number of successful user registrations");
    }

    public void incrementSuccessCount() {
        successfulRegistrations.increment();
    }
}

