package com.develop.orders_microservice.presentation.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    //Metodo que se encarga de manejar la respuesta de los servicios REST cada vez que se lance una BadRequestException
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {

        //Se crea un map que contendra la informacion de la respuesta en el body
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("Error", HttpStatus.BAD_REQUEST.toString());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    //Metodo que se encarga de manejar la respuesta de los servicios REST cada vez que se lance una ResourceNotFoundException
    protected ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {

        //Se crea un map que contendra la informacion de la respuesta en el body
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("Error", HttpStatus.NOT_FOUND.toString());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        // Crear un mapa con la información de la respuesta
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Internal Server Error: " + ex.getMessage());
        body.put("Error", HttpStatus.INTERNAL_SERVER_ERROR.toString());

        // Retornar una respuesta con código 500
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Validation failed");
        body.put("details", ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.transaction.TransactionSystemException.class)
    protected ResponseEntity<Object> handleTransactionSystemException(org.springframework.transaction.TransactionSystemException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());

        // Verificar si la causa raíz es una ConstraintViolationException
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof ConstraintViolationException constraintEx) {
            // Extraer y formatear los mensajes de las violaciones
            body.put("errors", constraintEx.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList()));
        } else {
            body.put("message", "Error al procesar la transacción");
        }

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
