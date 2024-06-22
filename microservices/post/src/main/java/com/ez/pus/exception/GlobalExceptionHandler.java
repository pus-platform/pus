package com.ez.pus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * This class extends ResponseEntityExceptionHandler to handle exceptions across the whole application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles ResourceNotFoundExceptions.
     *
     * @param exception The exception that was thrown.
     * @param webRequest The current web request.
     * @return A ResponseEntity containing the details of the error.
     */
    @ExceptionHandler(CustomExceptionHandling.ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(CustomExceptionHandling.ResourceNotFoundException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                exception.getResourceName().toUpperCase() + "_NOT_FOUND"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles EmailAlreadyExistsExceptions.
     *
     * @param exception The exception that was thrown.
     * @param webRequest The current web request.
     * @return A ResponseEntity containing the details of the error.
     */
    @ExceptionHandler(CustomExceptionHandling.EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleEmailAlreadyExistsException(CustomExceptionHandling.EmailAlreadyExistsException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "EMAIL_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UserNameAlreadyExistsExceptions.
     *
     * @param exception The exception that was thrown.
     * @param webRequest The current web request.
     * @return A ResponseEntity containing the details of the error.
     */
    @ExceptionHandler(CustomExceptionHandling.UserNameAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleUserNameAlreadyExistsException(CustomExceptionHandling.UserNameAlreadyExistsException exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "USERNAME_ALREADY_EXISTS"
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions.
     *
     * @param exception The exception that was thrown.
     * @param webRequest The current web request.
     * @return A ResponseEntity containing the details of the error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "INTERNAL_SERVER_ERROR"
        );
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(errorDetails);
    }
}
