package com.example.springsecurity.service;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.entity.VerificationToken;
import com.example.springsecurity.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationtoken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswrdResetToken(String token);

    void changePassword(User user, String newPassword);
}
