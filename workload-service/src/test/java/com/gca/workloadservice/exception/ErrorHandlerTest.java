package com.gca.workloadservice.exception;

import com.gca.openapi.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static com.gca.workloadservice.exception.ApiError.INVALID_REQUEST_ERROR;
import static com.gca.workloadservice.exception.ApiError.SERVER_ERROR;
import static com.gca.workloadservice.exception.ApiError.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidationExceptions_shouldReturnValidationErrorWithMessage() {
        String violationMessage = "Field must not be blank";
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        when(violation.getMessage()).thenReturn(violationMessage);

        ResponseEntity<ErrorResponse> actual = errorHandler.handleValidationExceptions(ex);

        assertNotNull(actual.getBody());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        assertEquals(VALIDATION_ERROR.getCode(), actual.getBody().getErrorCode());
        assertTrue(actual.getBody().getErrorMessage().contains(VALIDATION_ERROR.getMessage()));
        assertTrue(actual.getBody().getErrorMessage().contains(violationMessage));
    }

    @Test
    void handleUnhandledExceptions_shouldReturnServerError() {
        RuntimeException ex = new RuntimeException("Unknown runtime issue");

        ResponseEntity<ErrorResponse> actual = errorHandler.handleUnhandledExceptions(ex);

        assertNotNull(actual.getBody());
        assertEquals(INTERNAL_SERVER_ERROR, actual.getStatusCode());
        assertEquals(SERVER_ERROR.getCode(), actual.getBody().getErrorCode());
        assertEquals(SERVER_ERROR.getMessage(), actual.getBody().getErrorMessage());
    }

    @Test
    void handleNullPointerExceptions_shouldReturnValidationError() {
        NullPointerException ex = new NullPointerException("Something was null");

        ResponseEntity<ErrorResponse> actual = errorHandler.handleValidationExceptions(ex);

        assertNotNull(actual.getBody());
        assertEquals(BAD_REQUEST, actual.getStatusCode());
        assertEquals(INVALID_REQUEST_ERROR.getCode(), actual.getBody().getErrorCode());
        assertEquals(INVALID_REQUEST_ERROR.getMessage(), actual.getBody().getErrorMessage());
    }
}