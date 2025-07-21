package com.ecommerceapp.dubizlelike.service;

import com.ecommerceapp.dubizlelike.dto.ChangePasswordRequestDto;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;

public interface UserService {

    ServiceReturn<Void> forgotPassword(String userEmail);

    ServiceReturn<Void> resetPassword(String otp, String newPassword);

    ServiceReturn<Void> changePassword (ChangePasswordRequestDto changePasswordRequestDto);
}
