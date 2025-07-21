package com.ecommerceapp.dubizlelike.service.impl;

import com.ecommerceapp.dubizlelike.dto.EmailRequestDto;
import com.ecommerceapp.dubizlelike.enums.OtpType;
import com.ecommerceapp.dubizlelike.exception.NotFoundException;
import com.ecommerceapp.dubizlelike.model.OTP;
import com.ecommerceapp.dubizlelike.model.User;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;
import com.ecommerceapp.dubizlelike.repository.OtpRepository;
import com.ecommerceapp.dubizlelike.repository.UserRepository;
import com.ecommerceapp.dubizlelike.service.OtpService;
import com.ecommerceapp.dubizlelike.utils.email.MailSenderStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final MailSenderStrategy smtpMailSenderService;

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(900000) + 100000; // ensure a 6-digit otp
        return String.valueOf(otp);
    }

    public ServiceReturn<String> sendOtp(String emailAddress, OtpType type, Long expirationTimeMinutes) {

        /* Before sending an otp, we need to make sure the email address
         * belongs to a user that is registered!
         * */
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new NotFoundException("User"));

        String code = generateOtp();
        OTP otp = OTP.builder()
                .user(user)
                .used(false)
                .code(code)
                .expirationTime(LocalDateTime.now().plusMinutes(expirationTimeMinutes))
                .otpType(type)
                .build();
        otpRepository.save(otp);

        EmailRequestDto request = EmailRequestDto.builder()
                .to(emailAddress)
                .subject("OTP")
                .isHtml(false)
                .content(code)
                .build();
        smtpMailSenderService.sendEmail(request);

        return ServiceReturn.<String>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("OTP sent to email address: " + emailAddress)
                .data(null)
                .build();
    }

    public ServiceReturn<Boolean> validateOtp(String code) {
        OTP otp = otpRepository.findByCodeAndUsedFalseAndExpirationTimeAfter(code, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("OTP"));

        User user = otp.getUser();
        boolean isVerified = false;

        if (otp.getExpirationTime().isAfter(LocalDateTime.now()) && Boolean.FALSE.equals(user.getIsAccountVerified())) {
            user.setIsAccountVerified(true);
            userRepository.save(user);
            otpRepository.delete(otp);
            isVerified = true;
        }

        return ServiceReturn.<Boolean>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(isVerified ? HttpStatus.OK.value() : HttpStatus.BAD_REQUEST.value())
                .success(true)
                .message("Account verified successfully!")
                .data(null)
                .build();
    }

    public ServiceReturn<Void> invalidateOtp(String code) {
        OTP otp = otpRepository.findByCodeAndUsedFalseAndExpirationTimeAfter(code, LocalDateTime.now())
                .orElseThrow(() -> new NotFoundException("OTP"));

        otpRepository.delete(otp);
        return ServiceReturn.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .data(null)
                .build();
    }
}
