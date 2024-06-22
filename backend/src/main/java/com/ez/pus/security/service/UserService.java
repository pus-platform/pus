package com.ez.pus.security.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ez.pus.SendEmail.service.EmailSenderService;
import com.ez.pus.model.User;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.payload.request.SignupRequest;

@Service
public class UserService {

    private UserRepository userRepository;

    private EmailSenderService emailSenderService;

    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(SignupRequest signupRequest) {
        User newUser = new User();
        newUser.setUsername(signupRequest.getUsername());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setFullname(signupRequest.getFullname());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        String randomCode = UUID.randomUUID().toString();
        newUser.setVerificationCode(randomCode);
        newUser.setVerified(false);

        userRepository.save(newUser);

        emailSenderService.sendEmail(newUser.getEmail(), "Verify your email",
            "Please use the following code to verify your email and activate your account: " + randomCode);

        return newUser;
    }
}


