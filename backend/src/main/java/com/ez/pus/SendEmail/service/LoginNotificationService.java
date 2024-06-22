package com.ez.pus.SendEmail.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ez.pus.repository.*;
import com.ez.pus.SendEmail.bean.EmailData;
import com.ez.pus.model.User;

@Service
public class LoginNotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public void sendLoginNotification(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User loggedInUser = user.get();
            EmailData emailData = new EmailData();
            emailData.setReceipient(loggedInUser.getEmail());
            emailData.setMailSubject("Login Notification");
            emailData.setMailBody("Hello " + loggedInUser.getFullname() + ", you have successfully logged in.");

            notificationService.sendSimpleMail(emailData);
        }
    }
}
