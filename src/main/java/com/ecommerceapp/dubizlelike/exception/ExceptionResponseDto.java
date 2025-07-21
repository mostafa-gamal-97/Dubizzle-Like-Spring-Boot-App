package com.ecommerceapp.dubizlelike.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
// Exclude null fields when
// an instance of 'ExceptionResponseDto' is serialized to JSON.
public class ExceptionResponseDto {

    private final Date timeStamp = new Date();
    private final Object message;
    private final String status;
    private final String error;
    private final String path;
    private final String exception;
    private final Throwable cause;
}
