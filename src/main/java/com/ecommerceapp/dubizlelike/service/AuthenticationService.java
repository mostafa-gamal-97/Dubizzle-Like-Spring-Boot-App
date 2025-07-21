package com.ecommerceapp.dubizlelike.service;

import com.ecommerceapp.dubizlelike.dto.LoginRequestDto;
import com.ecommerceapp.dubizlelike.dto.LoginResponseDto;
import com.ecommerceapp.dubizlelike.dto.RefreshTokenRequestDto;
import com.ecommerceapp.dubizlelike.dto.RegisterRequestBody;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;

public interface AuthenticationService {

    ServiceReturn<Void> register (RegisterRequestBody request);
    ServiceReturn<LoginResponseDto> authenticate(LoginRequestDto request);
    ServiceReturn<LoginResponseDto> refreshToken(RefreshTokenRequestDto request);
}
