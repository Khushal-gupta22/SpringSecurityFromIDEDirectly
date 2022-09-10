package com.example.springsecurity.event.listener;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.event.RegistrationCompleteEvent;
import com.example.springsecurity.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegestrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // Create the verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        //Send mail to user
        String url =
                event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        // sendVerificationEmail()
        log.info("Click the link to verify your account: {} ", url);
    }
}
