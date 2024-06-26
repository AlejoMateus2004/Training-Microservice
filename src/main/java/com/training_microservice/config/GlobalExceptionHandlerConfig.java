package com.training_microservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandlerConfig extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerConfig.class);

    @ExceptionHandler(value = { NullPointerException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNullPointerException(Exception ex, WebRequest request) {
        logger.error("Null Pointer Exception or Invalid Argument Exception", ex);
        return handleExceptionInternal(ex, "Null pointer or invalid argument", null, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        logger.error("Unauthorized access", ex);
        return handleExceptionInternal(ex, "Unauthorized access", null, HttpStatus.FORBIDDEN, request);
    }


//    @ExceptionHandler(value = { UsernameNotFoundException.class })
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
//        logger.error("Username not found", ex);
//        return handleExceptionInternal(ex, "Username not found", null, HttpStatus.NOT_FOUND, request);
//    }
//
//    @ExceptionHandler(value = { DisabledException.class })
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public ResponseEntity<Object> handleDisabledException(DisabledException ex, WebRequest request) {
//        logger.error("User account is disabled", ex);
//        return handleExceptionInternal(ex, "User account is disabled", null, HttpStatus.FORBIDDEN, request);
//    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        logger.error("Data integrity violation in the database", ex);
        return handleExceptionInternal(ex, "Data integrity violation in the database", null, HttpStatus.CONFLICT, request);
    }


    record ExceptionResponse(String message, String code, String description){};

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, WebRequest request) {

        String message = String.format("Error in invocation of %s, Error message of %s",
                ((ServletWebRequest) request).getRequest().getRequestURL().toString(),
                ex.getMessage()
                );
        logger.error( message );

        return new ResponseEntity<>(
                new ExceptionResponse( message, "EX001", "Exception generated " )
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {

        String message = String.format("Error in invocation of %s, Error message of %s",
                ((ServletWebRequest) request).getRequest().getRequestURL().toString(),
                ex.getMessage()
        );
        logger.error( message );

        return new ResponseEntity<>(
                new ExceptionResponse( message, "EX001", "Runtime Exception generated " )
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<Object> handleArrayIndexOutOfBoundsException(
            ArrayIndexOutOfBoundsException ex, WebRequest request) {
        logger.error("Array Index Out of Bounds", ex);

        String errorMessage = "Error: Array Index Out of Bounds. " + ex.getMessage();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
