package com.gca.security.jwt;

import com.gca.exception.TokenRefreshException;
import com.gca.model.jwt.RefreshToken;
import com.gca.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.duration}")
    private Long jwtRefreshDuration;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String username) {
        deleteByUsername(username);

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .expiryDate(Instant.now().plusMillis(jwtRefreshDuration))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.debug("Created refresh token for user: {}", username);

        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signing request");
        }
        return token;
    }

    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
        log.debug("Deleted refresh tokens for username: {}", username);
    }
}