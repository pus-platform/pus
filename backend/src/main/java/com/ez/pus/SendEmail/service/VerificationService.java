package com.ez.pus.SendEmail.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.ez.pus.model.User;
import com.ez.pus.repository.UserRepository;

@Service
public class VerificationService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    public VerificationService(UserRepository userRepository, EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
    }

    public void sendVerificationEmail(User user) {
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        userRepository.save(user);

        String emailContent = "Please verify your account using this code: " + code;
        emailSenderService.sendEmail(user.getEmail(), "Verify Your Account", emailContent);
    }

    public boolean verifyUser(String username, String verificationCode) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && verificationCode.equals(user.getVerificationCode())) {
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate a 6-digit number
        return String.valueOf(code);
    }
}