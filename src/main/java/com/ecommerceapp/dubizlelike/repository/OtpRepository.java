package com.ecommerceapp.dubizlelike.repository;

import com.ecommerceapp.dubizlelike.model.OTP;
import com.ecommerceapp.dubizlelike.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByCodeAndUsedFalse(String code);
    Optional<OTP> findByCodeAndUsedFalseAndExpirationTimeAfter(String code, LocalDateTime expiryTime);
    Optional<OTP> findTopByUserOrderByExpirationTimeDesc(User user);
    Optional<OTP> findByUserAndCodeAndUsedFalse(User user, String code);
}
