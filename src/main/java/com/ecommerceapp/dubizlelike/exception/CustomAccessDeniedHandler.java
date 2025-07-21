package com.ecommerceapp.dubizlelike.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, @NotNull AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        String message = (accessDeniedException != null && accessDeniedException.getMessage() != null) ?
                accessDeniedException.getMessage() : "Authorization failed";
        HttpStatus statusCode = HttpStatus.FORBIDDEN;
        String exceptionStr = accessDeniedException!= null ? accessDeniedException.toString() : "";

        // Construct the exception response
        ExceptionResponseDto exceptionResponse = ExceptionResponseDto.builder()
                .message(message)
                .status(ExceptionStatus.FAIL.toString())
                .error(statusCode.getReasonPhrase())
                .path(request.getRequestURI())
                .exception(exceptionStr)
                .cause(accessDeniedException.getCause())
                .build();

        response.getWriter().write(exceptionResponse.toString());
    }
}
