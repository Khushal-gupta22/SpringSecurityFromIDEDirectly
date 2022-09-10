package com.example.springsecurity.repository;

import com.example.springsecurity.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public interface PasswordResetTokenRepository extends
        JpaRepository<PasswordResetToken, Long> {

    static PasswordResetToken findByToken(String token) {
        return null;
    }
}
