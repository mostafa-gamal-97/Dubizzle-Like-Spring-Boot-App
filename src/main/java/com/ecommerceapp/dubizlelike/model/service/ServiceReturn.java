package com.ecommerceapp.dubizlelike.model.service;

import com.ecommerceapp.dubizlelike.exception.ExceptionResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServiceReturn<T> {

    @JsonProperty("timestamp")
    private LocalDateTime timeStamp;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("Data")
    private T data;

    @JsonProperty("error")
    private ExceptionResponseDto error;
}
