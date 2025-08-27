package com.gca.security;

import com.gca.actuator.prometheus.AuthenticationMetrics;
import com.gca.dto.auth.AuthTokensDTO;
import com.gca.dto.auth.AuthenticationRequestDTO;
import com.gca.exception.AccountLockedException;
import com.gca.exception.TokenRefreshException;
import com.gca.exception.UserNotAuthenticatedException;
import com.gca.model.User;
import com.gca.model.jwt.RefreshToken;
import com.gca.repository.UserRepository;
import com.gca.security.jwt.AccessTokenService;
import com.gca.security.jwt.RefreshTokenService;
import com.gca.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Component
@Slf4j
@Validated
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    private final AuthenticationMetrics metrics;

    public AuthTokensDTO authenticate(@Valid AuthenticationRequestDTO request) {

        if (loginAttemptService.isBlocked(request.getUsername())) {
            throw new AccountLockedException();
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (isNotAuthenticated(user, request.getPassword())) {
            loginAttemptService.loginFailed(request.getUsername());
            metrics.recordFailedLogin();

            throw new UserNotAuthenticatedException("Wrong user credentials");
        }

        loginAttemptService.loginSucceeded(request.getUsername());
        metrics.recordSuccessfulLogin();

        String accessToken = accessTokenService.createAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        log.info("Authenticated user: {}", user.getUsername());
        return new AuthTokensDTO(accessToken, refreshToken.getToken());
    }

    public AuthTokensDTO refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid"));

        String username = refreshToken.getUsername();
        String newAccessToken = accessTokenService.createAccessToken(username);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(username);

        log.info("Token refreshed for user: {}", username);
        return new AuthTokensDTO(newAccessToken, newRefreshToken.getToken());
    }

    public void logout(String username) {
        refreshTokenService.deleteByUsername(username);
        log.info("User logged out: {}", username);
    }

    private boolean isNotAuthenticated(User user, String rawPassword) {
        return !userService.isUserCredentialsValid(user.getUsername(), rawPassword)
                || !user.getIsActive();
    }
}
