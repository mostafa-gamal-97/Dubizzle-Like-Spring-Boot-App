package com.ecommerceapp.dubizlelike.service.impl;

import com.ecommerceapp.dubizlelike.dto.ChangePasswordRequestDto;
import com.ecommerceapp.dubizlelike.enums.OtpType;
import com.ecommerceapp.dubizlelike.exception.InvalidDataException;
import com.ecommerceapp.dubizlelike.exception.NotFoundException;
import com.ecommerceapp.dubizlelike.exception.UnauthorizedException;
import com.ecommerceapp.dubizlelike.model.OTP;
import com.ecommerceapp.dubizlelike.model.User;
import com.ecommerceapp.dubizlelike.model.UserPasswordHistory;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;
import com.ecommerceapp.dubizlelike.repository.OtpRepository;
import com.ecommerceapp.dubizlelike.repository.UserPasswordResetRepository;
import com.ecommerceapp.dubizlelike.repository.UserRepository;
import com.ecommerceapp.dubizlelike.service.OtpService;
import com.ecommerceapp.dubizlelike.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final UserPasswordResetRepository passwordHistoryRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ServiceReturn<Void> forgotPassword(String userEmail) {
        User user = userRepository.findByEmailAddress(userEmail)
                .orElseThrow(() -> new NotFoundException("User"));

        if (Boolean.FALSE.equals(user.getIsAccountVerified())){
            throw new UnauthorizedException("User is not verified. Please verify by sending an otp first to the email address!");
        }
        otpService.sendOtp(userEmail, OtpType.RESET_PASSWORD, 10L);

        return ServiceReturn.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .data(null)
                .build();
    }

    @Override
    public ServiceReturn<Void> resetPassword(String otp, String newPassword) {
        // Make sure otp is valid and user is a verified user
        Optional<OTP> otpOptional = otpRepository.findByCodeAndUsedFalseAndExpirationTimeAfter(otp, LocalDateTime.now());
        if (otpOptional.isEmpty()) {
            throw new InvalidDataException("OTP is invalid or expired!");
        }

        User user = otpOptional.get().getUser();
        // Save current password hash to history before overwriting
        UserPasswordHistory userPasswordHistory = UserPasswordHistory.builder()
                .user(user)
                .oldPasswordHash(user.getPassword())
                .passwordChangedAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .password(passwordEncoder.encode(newPassword))
                .build();

        passwordHistoryRepository.save(userPasswordHistory);
        userRepository.save(user);
        otpRepository.delete(otpOptional.get());

        return ServiceReturn.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Password reset successfully!")
                .data(null)
                .build();
    }

    @Override
    public ServiceReturn<Void> changePassword(ChangePasswordRequestDto request) {
        User user = userRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new NotFoundException("User"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException("Old password is incorrect!");
        }

        UserPasswordHistory userPasswordHistory = UserPasswordHistory.builder()
                .user(user)
                .oldPasswordHash(passwordEncoder.encode(request.getOldPassword()))
                .passwordChangedAt(LocalDateTime.now())
                .build();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        passwordHistoryRepository.save(userPasswordHistory);
        userRepository.save(user);

        return ServiceReturn.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Password changed successfully!")
                .data(null)
                .build();
    }


}
