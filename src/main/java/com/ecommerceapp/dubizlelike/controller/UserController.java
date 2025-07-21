package com.ecommerceapp.dubizlelike.controller;

import com.ecommerceapp.dubizlelike.dto.ChangePasswordRequestDto;
import com.ecommerceapp.dubizlelike.dto.ResetPasswordRequestDto;
import com.ecommerceapp.dubizlelike.dto.SendOtpRequestDto;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;
import com.ecommerceapp.dubizlelike.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ServiceReturn<Void>> forgotPassword (@RequestBody SendOtpRequestDto request) {
         ServiceReturn<Void> response = userService.forgotPassword(request.getEmailAddress());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ServiceReturn<Void>> resetPassword (@RequestBody ResetPasswordRequestDto request) {
        ServiceReturn<Void> response = userService.resetPassword(request.getOtp(), request.getNewPassword());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ServiceReturn<Void>> changePassword (@RequestBody ChangePasswordRequestDto request) {
        ServiceReturn<Void> response = userService.changePassword(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
