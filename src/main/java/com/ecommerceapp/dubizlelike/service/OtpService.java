package com.ecommerceapp.dubizlelike.service;

import com.ecommerceapp.dubizlelike.enums.OtpType;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;

public interface OtpService {

    ServiceReturn<String> sendOtp(String emailAddress, OtpType type, Long otpExpirationTimeMinutes);
    ServiceReturn<Boolean> validateOtp(String code);
    ServiceReturn<Void> invalidateOtp(String code);
}
