package com.ez.pus.security.service;

import com.ez.pus.security.payload.request.SignupRequest;
import com.ez.pus.user.User;
import com.ez.pus.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(SignupRequest signupRequest) {
        User newUser = new User();
        newUser.setUsername(signupRequest.getUsername());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setFullname(signupRequest.getFullname());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userRepository.save(newUser);
        return newUser;
    }
}
