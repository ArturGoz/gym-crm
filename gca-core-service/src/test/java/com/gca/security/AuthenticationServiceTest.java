package com.gca.security;

import com.gca.actuator.prometheus.AuthenticationMetrics;
import com.gca.dto.auth.AuthTokensDTO;
import com.gca.dto.auth.AuthenticationRequestDTO;
import com.gca.exception.AccountLockedException;
import com.gca.exception.UserNotAuthenticatedException;
import com.gca.model.User;
import com.gca.model.jwt.RefreshToken;
import com.gca.repository.UserRepository;
import com.gca.security.jwt.AccessTokenService;
import com.gca.security.jwt.RefreshTokenService;
import com.gca.service.UserService;
import com.gca.utils.GymTestProvider;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private final AuthenticationRequestDTO request = new AuthenticationRequestDTO(
            "arnold.schwarzenegger", "securePass123");

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationMetrics metrics;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService service;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = GymTestProvider.constructUser();
    }

    @Test
    void authenticate_successful() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(REFRESH_TOKEN)
                .username(activeUser.getUsername())
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(loginAttemptService.isBlocked(request.getUsername())).thenReturn(false);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(activeUser));
        when(userService.isUserCredentialsValid(request.getUsername(), request.getPassword())).thenReturn(true);
        when(accessTokenService.createAccessToken(activeUser.getUsername())).thenReturn(ACCESS_TOKEN);
        when(refreshTokenService.createRefreshToken(activeUser.getUsername())).thenReturn(refreshToken);

        AuthTokensDTO actual = service.authenticate(request);

        assertEquals(ACCESS_TOKEN, actual.getAccessToken());
        assertEquals(REFRESH_TOKEN, actual.getRefreshToken());
        verify(loginAttemptService).isBlocked(request.getUsername());
        verify(userRepository).findByUsername(request.getUsername());
        verify(userService).isUserCredentialsValid(request.getUsername(), request.getPassword());
        verify(loginAttemptService).loginSucceeded(request.getUsername());
        verify(metrics).recordSuccessfulLogin();
    }

    @Test
    void authenticate_userBlocked_throwsAccountLockedException() {
        when(loginAttemptService.isBlocked(request.getUsername())).thenReturn(true);

        AccountLockedException actual = assertThrows(AccountLockedException.class,
                () -> service.authenticate(request));

        assertNotNull(actual);
        verify(loginAttemptService).isBlocked(request.getUsername());
        verifyNoMoreInteractions(loginAttemptService, userRepository, userService, metrics);
    }

    @Test
    void authenticate_userNotFound_throwsException() {
        when(loginAttemptService.isBlocked(request.getUsername())).thenReturn(false);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> service.authenticate(request));

        assertNotNull(actual);
        verify(loginAttemptService).isBlocked(request.getUsername());
        verify(userRepository).findByUsername(request.getUsername());
    }

    @Test
    void authenticate_wrongPassword_throwsException() {
        when(loginAttemptService.isBlocked(request.getUsername())).thenReturn(false);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(activeUser));
        when(userService.isUserCredentialsValid(request.getUsername(), request.getPassword())).thenReturn(false);

        UserNotAuthenticatedException actual = assertThrows(UserNotAuthenticatedException.class,
                () -> service.authenticate(request));

        assertNotNull(actual);
        verify(loginAttemptService).isBlocked(request.getUsername());
        verify(userRepository).findByUsername(request.getUsername());
        verify(userService).isUserCredentialsValid(request.getUsername(), request.getPassword());
        verify(loginAttemptService).loginFailed(request.getUsername());
        verify(metrics).recordFailedLogin();
    }

    @Test
    void authenticate_inactiveUser_throwsException() {
        activeUser.setIsActive(false);

        when(loginAttemptService.isBlocked(request.getUsername())).thenReturn(false);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(activeUser));
        when(userService.isUserCredentialsValid(request.getUsername(), request.getPassword())).thenReturn(true);

        UserNotAuthenticatedException actual = assertThrows(UserNotAuthenticatedException.class,
                () -> service.authenticate(request));

        assertNotNull(actual);
        verify(loginAttemptService).isBlocked(request.getUsername());
        verify(userRepository).findByUsername(request.getUsername());
        verify(userService).isUserCredentialsValid(request.getUsername(), request.getPassword());
    }
}
