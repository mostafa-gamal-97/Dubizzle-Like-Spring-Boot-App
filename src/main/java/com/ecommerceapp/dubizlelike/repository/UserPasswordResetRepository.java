package com.ecommerceapp.dubizlelike.repository;

import com.ecommerceapp.dubizlelike.model.UserPasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPasswordResetRepository extends JpaRepository<UserPasswordHistory, Long> {

}
