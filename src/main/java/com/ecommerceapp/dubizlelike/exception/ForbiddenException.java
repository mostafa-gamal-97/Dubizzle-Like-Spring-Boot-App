package com.ecommerceapp.dubizlelike.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomException{

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
