package com.gca.security.jwt;

import com.gca.exception.TokenRefreshException;
import com.gca.model.jwt.RefreshToken;
import com.gca.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "jwtRefreshDuration", 3600_000L);
    }

    @Test
    void findByToken_shouldReturnOptionalRefreshToken() {
        String expectedToken = "test-token";
        RefreshToken refreshToken = RefreshToken.builder().token(expectedToken).build();

        when(repository.findByToken(expectedToken)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> actual = service.findByToken(expectedToken);

        assertTrue(actual.isPresent());
        assertEquals(expectedToken, actual.get().getToken());
        verify(repository).findByToken(expectedToken);
    }

    @Test
    void createRefreshToken_shouldDeleteOldAndSaveNew() {
        String expectedUsername = "user123";
        RefreshToken savedToken = RefreshToken.builder()
                .username(expectedUsername)
                .token("new-token")
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        when(repository.save(any(RefreshToken.class))).thenReturn(savedToken);

        RefreshToken actual = service.createRefreshToken(expectedUsername);

        assertNotNull(actual);
        assertEquals(expectedUsername, actual.getUsername());
        assertNotNull(actual.getToken());
        assertTrue(actual.getExpiryDate().isAfter(Instant.now()));
        verify(repository).deleteByUsername(expectedUsername);
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    void verifyExpiration_shouldReturnToken_whenNotExpired() {
        RefreshToken expected = RefreshToken.builder()
                .token("valid-token")
                .expiryDate(Instant.now().plusSeconds(60))
                .build();

        RefreshToken actual = service.verifyExpiration(expected);

        assertSame(expected, actual);
        verify(repository, never()).delete(any());
    }

    @Test
    void verifyExpiration_shouldThrowException_whenExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("expired-token")
                .expiryDate(Instant.now().minusSeconds(60))
                .build();

        TokenRefreshException ex = assertThrows(
                TokenRefreshException.class,
                () -> service.verifyExpiration(token));

        assertTrue(ex.getMessage().contains("Refresh token was expired"));
        verify(repository).delete(token);
    }

    @Test
    void deleteByUsername_shouldCallRepositoryDelete() {
        String username = "user123";
        service.deleteByUsername(username);
        verify(repository).deleteByUsername(username);
    }
}
