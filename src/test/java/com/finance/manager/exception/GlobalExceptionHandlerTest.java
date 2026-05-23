package com.finance.manager.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleBadRequest → 400")
    void handleBadRequest() {
        ResponseEntity<Map<String, String>> resp = handler.handleBadRequest(
                new BadRequestException("bad input"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).containsEntry("message", "bad input");
    }

    @Test
    @DisplayName("handleUnauthorized → 401")
    void handleUnauthorized() {
        ResponseEntity<Map<String, String>> resp = handler.handleUnauthorized(
                new UnauthorizedException("not authenticated"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody()).containsEntry("message", "not authenticated");
    }

    @Test
    @DisplayName("handleForbidden → 403")
    void handleForbidden() {
        ResponseEntity<Map<String, String>> resp = handler.handleForbidden(
                new ForbiddenException("access denied"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(resp.getBody()).containsEntry("message", "access denied");
    }

    @Test
    @DisplayName("handleNotFound → 404")
    void handleNotFound() {
        ResponseEntity<Map<String, String>> resp = handler.handleNotFound(
                new ResourceNotFoundException("not found"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).containsEntry("message", "not found");
    }

    @Test
    @DisplayName("handleConflict → 409")
    void handleConflict() {
        ResponseEntity<Map<String, String>> resp = handler.handleConflict(
                new ConflictException("already exists"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody()).containsEntry("message", "already exists");
    }

    @Test
    @DisplayName("handleGeneric → 500")
    void handleGeneric() {
        ResponseEntity<Map<String, String>> resp = handler.handleGeneric(
                new RuntimeException("oops"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().get("message")).contains("oops");
    }

    @Test
    @DisplayName("handleValidation → 400 with field errors map")
    void handleValidation() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("request", "amount", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> resp = handler.handleValidation(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).containsEntry("message", "Validation failed");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) resp.getBody().get("errors");
        assertThat(errors).containsEntry("amount", "must not be null");
    }
}
