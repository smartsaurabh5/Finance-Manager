package com.finance.manager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    void handlesNotFoundConflictForbiddenAndBadRequest() {
        assertThat(handler.handleResourceNotFoundException(new ResourceNotFoundException("missing"), request).getStatusCode().value()).isEqualTo(404);
        assertThat(handler.handleConflictException(new ConflictException("duplicate"), request).getStatusCode().value()).isEqualTo(409);
        assertThat(handler.handleForbiddenException(new ForbiddenException("nope"), request).getStatusCode().value()).isEqualTo(403);
        assertThat(handler.handleIllegalArgumentException(new IllegalArgumentException("bad"), request).getStatusCode().value()).isEqualTo(400);
        assertThat(handler.handleAccountLockedException(new AccountLockedException("locked"), request).getStatusCode().value()).isEqualTo(423);
    }

    @Test
    void handlesValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "Name is required"));
        Method method = Sample.class.getDeclaredMethod("sample", String.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        var response = handler.handleValidationExceptions(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getValidationErrors()).containsEntry("name", "Name is required");
    }

    private static class Sample {
        @SuppressWarnings("unused")
        void sample(String value) {
        }
    }
}
