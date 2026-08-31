package com.omardev.event_ticketing.exception;

import com.omardev.event_ticketing.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Global exception handler for consistent API error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business exceptions (custom)
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    /**
     * Handle validation errors (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    /**
     * Handle NOT FOUND
     */
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EventNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    /**
     * Handle forbidden access (user has no required role)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", List.of());
    }

    /**
     * Handle authentication errors (missing/invalid token)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", List.of());
    }

    /**
     * Fallback for unexpected errors (keep LAST)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {

        // Log full error with stack trace
        log.error("Unexpected error occurred", ex);

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                List.of()
        );
    }

    /**
     * Build consistent error response
     */
    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            List<String> errors
    ) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        status.value(),
                        message,
                        errors,
                        LocalDateTime.now()
                )
        );
    }
}