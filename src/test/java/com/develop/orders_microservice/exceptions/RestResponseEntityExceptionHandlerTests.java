package com.develop.orders_microservice.exceptions;

import com.develop.orders_microservice.presentation.exceptions.BadRequestException;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import com.develop.orders_microservice.presentation.exceptions.RestResponseEntityExceptionHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestResponseEntityExceptionHandlerTests {
    private TestableRestResponseEntityExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new TestableRestResponseEntityExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    static class TestableRestResponseEntityExceptionHandler extends RestResponseEntityExceptionHandler {
        @Override
        public ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
            return super.handleBadRequest(ex, request);
        }
        @Override
        public ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {
            return super.handleResourceNotFound(ex, request);
        }
        @Override
        public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
            return super.handleGenericException(ex, request);
        }
        @Override
        public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
            return super.handleConstraintViolation(ex, request);
        }
        @Override
        public ResponseEntity<Object> handleTransactionSystemException(org.springframework.transaction.TransactionSystemException ex, WebRequest request) {
            return super.handleTransactionSystemException(ex, request);
        }
    }

    @Test
    void testHandleBadRequestWithCustomException() {
        BadRequestException ex = new BadRequestException("Error de petición inválida");
        ResponseEntity<Object> response = handler.handleBadRequest(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Error de petición inválida", body.get("message"));
        assertEquals("400 BAD_REQUEST", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleBadRequestWithRuntimeException() {
        RuntimeException ex = new RuntimeException("Otro error de bad request");
        ResponseEntity<Object> response = handler.handleBadRequest(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Otro error de bad request", body.get("message"));
        assertEquals("400 BAD_REQUEST", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleResourceNotFoundWithCustomException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        ResponseEntity<Object> response = handler.handleResourceNotFound(ex, webRequest);

        assertEquals(404, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Recurso no encontrado", body.get("message"));
        assertEquals("404 NOT_FOUND", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleResourceNotFoundWithRuntimeException() {
        RuntimeException ex = new RuntimeException("Otro error de recurso no encontrado");
        ResponseEntity<Object> response = handler.handleResourceNotFound(ex, webRequest);

        assertEquals(404, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Otro error de recurso no encontrado", body.get("message"));
        assertEquals("404 NOT_FOUND", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Error genérico");
        ResponseEntity<Object> response = handler.handleGenericException(ex, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Internal Server Error: Error genérico", body.get("message"));
        assertEquals("500 INTERNAL_SERVER_ERROR", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleGenericExceptionWithNullMessage() {
        Exception ex = new Exception();
        ResponseEntity<Object> response = handler.handleGenericException(ex, webRequest);

        assertEquals(500, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue(((String) body.get("message")).startsWith("Internal Server Error:"));
        assertEquals("500 INTERNAL_SERVER_ERROR", body.get("Error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testHandleConstraintViolationWithSingleViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Campo requerido");

        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<Object> response = handler.handleConstraintViolation(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Validation failed", body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertTrue(((java.util.List<?>) body.get("details")).contains("Campo requerido"));
    }

    @Test
    void testHandleConstraintViolationWithNoViolations() {
        ConstraintViolationException ex = new ConstraintViolationException(Collections.emptySet());

        ResponseEntity<Object> response = handler.handleConstraintViolation(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Validation failed", body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertTrue(((java.util.List<?>) body.get("details")).isEmpty());
    }

    @Test
    void testHandleTransactionSystemExceptionWithConstraintViolations() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Debe ser mayor a 0");
        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException constraintEx = new ConstraintViolationException(violations);

        org.springframework.transaction.TransactionSystemException ex =
                mock(org.springframework.transaction.TransactionSystemException.class);
        when(ex.getRootCause()).thenReturn(constraintEx);

        ResponseEntity<Object> response = handler.handleTransactionSystemException(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertTrue(((java.util.List<?>) body.get("errors")).contains("Debe ser mayor a 0"));
    }

    @Test
    void testHandleTransactionSystemExceptionWithoutConstraintViolations() {
        org.springframework.transaction.TransactionSystemException ex =
                mock(org.springframework.transaction.TransactionSystemException.class);
        when(ex.getRootCause()).thenReturn(new RuntimeException("Otra causa"));

        ResponseEntity<Object> response = handler.handleTransactionSystemException(ex, webRequest);

        assertEquals(400, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("timestamp"));
        assertEquals("Error al procesar la transacción", body.get("message"));
    }

}
