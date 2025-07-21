package com.ecommerceapp.dubizlelike.service.impl;

import com.ecommerceapp.dubizlelike.dto.LoginRequestDto;
import com.ecommerceapp.dubizlelike.dto.LoginResponseDto;
import com.ecommerceapp.dubizlelike.dto.RefreshTokenRequestDto;
import com.ecommerceapp.dubizlelike.dto.RegisterRequestBody;
import com.ecommerceapp.dubizlelike.enums.OtpType;
import com.ecommerceapp.dubizlelike.enums.Role;
import com.ecommerceapp.dubizlelike.exception.NotFoundException;
import com.ecommerceapp.dubizlelike.exception.UnauthorizedException;
import com.ecommerceapp.dubizlelike.model.User;
import com.ecommerceapp.dubizlelike.model.service.ServiceReturn;
import com.ecommerceapp.dubizlelike.repository.UserRepository;
import com.ecommerceapp.dubizlelike.service.AuthenticationService;
import com.ecommerceapp.dubizlelike.utils.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;
    private final OtpServiceImpl otpService;

    public ServiceReturn<Void> register(RegisterRequestBody request) {
        var user = User.builder()
                .name(request.getName())
                .emailAddress(request.getEmailAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isAccountVerified(false)
                .build();
        userRepository.save(user);
        otpService.sendOtp(request.getEmailAddress(), OtpType.VERIFY_ACCOUNT, 30L);

        return ServiceReturn.<Void>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .data(null)
                .build();
    }

    public ServiceReturn<LoginResponseDto> authenticate(LoginRequestDto request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmailAddress(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password!");
        } catch (LockedException e) {
            throw new LockedException("User is locked!");
        } catch (DisabledException e) {
            throw new DisabledException("User is disabled!");
        }

        User user = userRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new NotFoundException("User"));

        if (Boolean.FALSE.equals(user.getIsAccountVerified())) {
            throw new UnauthorizedException("User is not verified. Please verify by sending an otp first to the email address!");
        }

        LoginResponseDto loginResponseDto = generateTokenPair(user.getEmailAddress());

        return ServiceReturn.<LoginResponseDto>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .data(loginResponseDto)
                .error(null)
                .build();
    }

    private LoginResponseDto generateTokenPair(String userEmailAddress) {
        return LoginResponseDto.builder()
                .accessToken(jwtService.generateAccessToken(userEmailAddress))
                .refreshToken(jwtService.generateRefreshToken(userEmailAddress))
                .build();
    }

    public ServiceReturn<LoginResponseDto> refreshToken(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();
        String emailAddress = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new NotFoundException("User"));

        String userEmailAddress = user.getEmailAddress();

        boolean isTokenValid = jwtService.isTokenValid(refreshToken, userEmailAddress);
        if (!isTokenValid) {
            throw new UnauthorizedException("Refresh token is invalid!");
        }

        String newAccessToken = jwtService.generateAccessToken(userEmailAddress);
        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Return the original refresh token, Don't rotate it for now.
                .build();

        return ServiceReturn.<LoginResponseDto>builder()
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .data(response)
                .build();
    }

}
