package com.gca.exception;

import com.gca.openapi.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;
import java.util.stream.Collectors;

import static com.gca.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gca.exception.ApiError.DATABASE_ERROR;
import static com.gca.exception.ApiError.INVALID_REQUEST_ERROR;
import static com.gca.exception.ApiError.NOT_FOUND_ERROR;
import static com.gca.exception.ApiError.REFRESH_TOKEN_ERROR;
import static com.gca.exception.ApiError.SERVER_ERROR;
import static com.gca.exception.ApiError.TOO_MANY_REQUESTS_ERROR;
import static com.gca.exception.ApiError.VALIDATION_ERROR;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final Set<String> BAD_REQUEST_PREFIXES = Set.of(
            "Invalid trainee username",
            "Invalid training type",
            "Invalid trainer username",
            "Trainee username must be provided",
            "Trainer username must be provided",
            "Username must not be null",
            "Old password is wrong",
            "Active status is wrong"
    );

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        log.error("ServiceException: {}", ex.getMessage());
        ApiError error = resolveError(ex);

        return buildErrorResponse(error);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponse> handleTokenRefreshException(TokenRefreshException ex) {
        log.error("Exception with refresh token: {}", ex.getMessage());

        return buildErrorResponse(REFRESH_TOKEN_ERROR);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleDaoException(AccountLockedException ex) {
        log.error("Account Locked Exception: {}", ex.getMessage());

        return buildErrorResponse(TOO_MANY_REQUESTS_ERROR);
    }

    @ExceptionHandler(DaoException.class)
    public ResponseEntity<ErrorResponse> handleDaoException(DaoException ex) {
        log.error("Database Exception: {}", ex.getMessage());

        return buildErrorResponse(DATABASE_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundExceptions(Exception ex) {
        log.error("Entity not found Exception: {}", ex.getMessage());

        return buildErrorResponse(NOT_FOUND_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {
        log.error("Validation Exception: {}", ex.getMessage());

        return buildErrorResponse(VALIDATION_ERROR, extractValidationMessage(ex));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledExceptions(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage());

        return buildErrorResponse(SERVER_ERROR);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUserNotAuthenticatedExceptions(Exception ex) {
        log.error("Authentication Exception: {}", ex.getMessage());

        return buildErrorResponse(AUTHENTICATION_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError) {
        return buildErrorResponse(apiError, null);
    }

    private String extractValidationMessage(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ApiError apiError, String message) {
        message = StringUtils.isBlank(message) ? "" : message;

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(apiError.getCode());
        errorResponse.setErrorMessage(apiError.getMessage() + message);

        return new ResponseEntity<>(errorResponse, apiError.getHttpStatus());
    }

    private ApiError resolveError(ServiceException ex) {
        String normalizedMessage = StringUtils.isBlank(ex.getMessage()) ? "" : ex.getMessage().toLowerCase();

        return BAD_REQUEST_PREFIXES.stream()
                .map(String::toLowerCase)
                .filter(normalizedMessage::startsWith)
                .findFirst()
                .map(suffix -> INVALID_REQUEST_ERROR)
                .orElse(SERVER_ERROR);
    }
}
