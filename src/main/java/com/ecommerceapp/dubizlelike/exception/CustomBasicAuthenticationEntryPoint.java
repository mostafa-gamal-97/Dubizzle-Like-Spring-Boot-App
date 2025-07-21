package com.ecommerceapp.dubizlelike.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String message = (authException != null && authException.getMessage() != null) ?
                authException.getMessage() : "Unauthorized ";
        HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
        String exceptionStr = authException!= null ? authException.toString() : "";

        // Construct the exception response
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(message)
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(request.getRequestURI())
                .exception(exceptionStr)
                .cause(authException.getCause())
                .build();

        response.getWriter().write(exceptionResponse.toString());
    }
}
