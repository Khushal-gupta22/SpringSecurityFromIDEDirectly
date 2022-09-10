package com.example.springsecurity.service;

import com.example.springsecurity.entity.PasswordResetToken;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.entity.VerificationToken;
import com.example.springsecurity.model.UserModel;
import com.example.springsecurity.repository.PasswordResetTokenRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.repository.VerificationTokenRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken
                = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationtoken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);

        if(verificationToken == null) {
            return "Invalid" ;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() - cal.getTime().getTime() <=0) {
            verificationTokenRepository.delete(verificationToken);
            return " expired";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "valid";

    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken
                = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken =
                PasswordResetTokenRepository.findByToken(token);

        if(passwordResetToken == null) {
            return "Invalid" ;
        }

        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime() <=0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return " expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswrdResetToken(String token) {
        return Optional.ofNullable(PasswordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
