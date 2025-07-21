package com.ecommerceapp.dubizlelike.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestBody {

    private String name;
    private String emailAddress;
    private String password;
}
