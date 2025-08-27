package com.gca.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
        ReflectionTestUtils.setField(service, "maxAttempt", 3);
        ReflectionTestUtils.setField(service, "blockTimeMs", 50L);
    }

    @Test
    void loginSucceeded_shouldResetAttempts() {
        String username = "user1";

        service.loginFailed(username);
        service.loginFailed(username);

        assertThat(service.isBlocked(username)).isFalse();
    }

    @Test
    void loginFailed_shouldIncreaseAttemptCount() {
        String username = "user2";

        service.loginFailed(username);
        service.loginFailed(username);
        service.loginFailed(username);

        assertThat(service.isBlocked(username)).isTrue();
    }

    @Test
    void isBlocked_shouldReturnFalseIfNoAttempts() {
        assertThat(service.isBlocked("unknownUser")).isFalse();
    }

    @Test
    void isBlocked_shouldReturnFalseIfAttemptsExpired() throws InterruptedException {
        String username = "user3";

        service.loginFailed(username);
        service.loginFailed(username);
        service.loginFailed(username);

        assertThat(service.isBlocked(username)).isTrue();
        Thread.sleep(60);
        assertThat(service.isBlocked(username)).isFalse();
    }
}