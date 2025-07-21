package com.ecommerceapp.dubizlelike.controller;

import com.ecommerceapp.dubizlelike.dto.LoginRequestDto;
import com.ecommerceapp.dubizlelike.dto.LoginResponseDto;
import com.ecommerceapp.dubizlelike.dto.RefreshTokenRequestDto;
import com.ecommerceapp.dubizlelike.dto.RegisterRequestBody;
import com.ecommerceapp.dubizlelike.enums.OtpType;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;
import com.ecommerceapp.dubizlelike.service.impl.AuthenticationServiceImpl;
import com.ecommerceapp.dubizlelike.service.impl.OtpServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authService;
    private final OtpServiceImpl otpService;

    @PostMapping("/register")
    public ResponseEntity<ServiceReturn<Void>> register (@RequestBody RegisterRequestBody request) {
        ServiceReturn<Void> response = authService.register(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ServiceReturn<LoginResponseDto>> authenticate (@RequestBody LoginRequestDto request){
        ServiceReturn<LoginResponseDto> response = authService.authenticate(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ServiceReturn<LoginResponseDto>> refreshToken (@RequestBody RefreshTokenRequestDto request) {
        ServiceReturn<LoginResponseDto> response = authService.refreshToken(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ServiceReturn<String>> sendOtp(@RequestParam(name = "email_address") String emailAddress){
        ServiceReturn<String> response = otpService.sendOtp(emailAddress, OtpType.VERIFY_ACCOUNT, 30L);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ServiceReturn<Boolean>> validateOtp (@RequestParam(name = "email-address") String emailAddress, @RequestParam String otp) {
        ServiceReturn<Boolean> response = otpService.validateOtp(otp);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
