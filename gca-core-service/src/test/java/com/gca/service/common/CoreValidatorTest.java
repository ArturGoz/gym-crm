package com.gca.service.common;

import com.gca.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoreValidatorTest {

    private final CoreValidator validator = new CoreValidator();

    @ParameterizedTest
    @NullSource
    @EmptySource
    void shouldThrowException_whenUsernameIsNullOrEmpty(String blankUsername) {
        ServiceException ex = assertThrows(
                ServiceException.class,
                () -> validator.validateUsername(blankUsername)
        );

        assertEquals("Username must not be null or empty", ex.getMessage());
    }

    @Test
    void shouldNotThrowException_whenUsernameIsValid() {
        assertDoesNotThrow(() -> validator.validateUsername("valid.username"));
    }
}