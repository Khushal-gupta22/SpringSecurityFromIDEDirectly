package com.example.springsecurity.controller;


import com.example.springsecurity.entity.User;
import com.example.springsecurity.entity.VerificationToken;
import com.example.springsecurity.event.RegistrationCompleteEvent;
import com.example.springsecurity.model.PasswordModel;
import com.example.springsecurity.model.UserModel;
import com.example.springsecurity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;
    private HttpServletRequest request;


    @PostMapping("register/")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request)  {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return "Success";

    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationtoken(token);
        if(result.equalsIgnoreCase("valid")) {
            return "User Verified Successfully";

        } return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToke(@RequestParam("token") String oldToken,
                                         HttpServletRequest request) {

        VerificationToken verificationToken =
                userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user!= null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("savePassword")
    public String savepassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("vaild")){
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswrdResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password changed successfully";
        } else {
            return "Invalid Token";
        }

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl + "/savePassword?token=" + token;

        // sendVerificationEmail()
        log.info("Click the link to reset your password: {} ", url);
        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url =
                applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        // sendVerificationEmail()
        log.info("Click the link to verify your account: {} ", url);
    }


    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }
}
