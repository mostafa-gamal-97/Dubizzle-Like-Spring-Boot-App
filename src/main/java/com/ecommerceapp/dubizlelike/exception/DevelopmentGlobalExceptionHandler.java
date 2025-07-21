package com.ecommerceapp.dubizlelike.exception;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("dev")
@ControllerAdvice
@RequiredArgsConstructor
public class DevelopmentGlobalExceptionHandler {

    // Handle 'NoResourceFoundException'
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNoResourceFoundException(
            NoResourceFoundException ex, WebRequest request) {

        HttpStatus statusCode = HttpStatus.NOT_FOUND;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(request.getDescription(false) + " resource not found.")
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString())
                .cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // Handle request malformed / unreadable / missing body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {

        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(request.getDescription(false) + " Request body is missing or not readable. Please ensure the request body is properly formatted.")
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString())
                .cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // Handle validation errors for annotations (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationsEx(MethodArgumentNotValidException ex) {

        List<String> errorMessages = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errorMessages.add(errorMessage);
        });

        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(errorMessages)
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString())
                .cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // Handle validation errors for annotations (@Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {

        String[] messagesArray = ex.getMessage().split(", ");
        List<String> errorMessages = new ArrayList<>(Arrays.asList(messagesArray));

        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(errorMessages)
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString()).
                cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // Handle not authorized exceptions (Roles Exception)
    public ResponseEntity<ExceptionResponseDto> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {

        HttpStatus statusCode = HttpStatus.FORBIDDEN;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message("You do not have the necessary permissions to access this resource.")
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString()).cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // Handle my custom exception
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponseDto> handleCustomException(CustomException ex) {

        HttpStatus statusCode = ex.getStatusCode();
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString())
                .cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }

    // If the exception thrown is not of the above types, then this will be returned.
    public ResponseEntity<ExceptionResponseDto> handleException(Exception ex) {

        HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(ex.getMessage())
                .status(ExceptionStatus.ERROR.toString())
                .error(statusCode.getReasonPhrase())
                .path(ex.getStackTrace()[0].toString())
                .exception(ex.toString())
                .cause(ex.getCause())
                .build();

        return new ResponseEntity<>(exceptionResponse, statusCode);
    }
}
