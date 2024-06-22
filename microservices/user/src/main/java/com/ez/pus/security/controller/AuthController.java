package com.ez.pus.security.controller;

import com.ez.pus.enums.University;
import com.ez.pus.security.jwt.JwtUtils;
import com.ez.pus.security.payload.request.LoginRequest;
import com.ez.pus.security.payload.request.SignupRequest;
import com.ez.pus.security.payload.response.JwtResponse;
import com.ez.pus.security.payload.response.MessageResponse;
import com.ez.pus.security.service.UserDetailsImpl;
import com.ez.pus.user.User;
import com.ez.pus.user.UserDTO;
import com.ez.pus.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.trace("authenticateUser start");
        logger.debug("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        logger.info("User authenticated successfully: {}", userDetails.getUsername());

        logger.trace("authenticateUser end");
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail()));
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent())
                return ResponseEntity.ok(UserDTO.fromUser(user.get()));
            else
                return ResponseEntity.badRequest().body("invalid username in token");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.trace("registerUser start");
        logger.debug("Registering user: {}", signUpRequest.getUsername());

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            logger.warn("Username already taken: {}", signUpRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This Username is already in use!"));
        }

        if (!isEmailDomainValid(signUpRequest.getEmail())) {
            logger.warn("Invalid email domain for: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: The Email domain doesnt match the University"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Email already taken: {}", signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: This Email is already in use!"));
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .fullname(signUpRequest.getFullname())
                .build();
        userRepository.save(user);

        logger.info("User registered successfully: {}", signUpRequest.getUsername());

        logger.trace("registerUser end");
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private boolean isEmailDomainValid(String email) {
        logger.trace("isEmailDomainValid start");
        if (Arrays.stream(University.values()).anyMatch(uni -> uni.getDomain().equals(email.split("@")[1]))) {
            logger.debug("Email domain validation result for {} is {}", email, true);
            return true;
        } else {
            logger.error("Invalid email format: {}", email);
            return false;
        }
    }
}
