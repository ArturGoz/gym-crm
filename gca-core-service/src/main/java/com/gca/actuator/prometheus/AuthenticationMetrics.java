package com.gca.actuator.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMetrics {

    private final Counter successfulLogins;
    private final Counter failedLogins;

    public AuthenticationMetrics(MeterRegistry registry) {
        this.successfulLogins = registry.counter("auth.login.success.count",
                "description", "Number of successful user logins");
        this.failedLogins = registry.counter("auth.login.failed.count",
                "description", "Number of failed user logins");
    }

    public void recordSuccessfulLogin() {
        successfulLogins.increment();
    }

    public void recordFailedLogin() {
        failedLogins.increment();
    }
}
