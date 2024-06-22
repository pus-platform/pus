package com.ez.pus.security.controller;

import com.ez.pus.enums.Status;
import com.ez.pus.SendEmail.service.LoginNotificationService;
import com.ez.pus.SendEmail.service.VerificationService;
import com.ez.pus.dto.UserDTO;
import com.ez.pus.enums.University;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.User;
import com.ez.pus.repository.CommunityRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.JwtUtils;
import com.ez.pus.security.service.UserDetailsImpl;
import com.ez.pus.security.payload.request.LoginRequest;
import com.ez.pus.security.payload.request.SignupRequest;
import com.ez.pus.security.payload.request.VerificationRequest;
import com.ez.pus.security.payload.response.JwtResponse;
import com.ez.pus.security.payload.response.MessageResponse;
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

@CrossOrigin(origins = {"*", "http://localhost:5173", "http://localhsot:3000"}, maxAge = 86400)
@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final LoginNotificationService loginNotificationService;
    private final VerificationService verificationService;

    @PostMapping("/get-google-token")
    public ResponseEntity<?> handleGoogleRedirect(@RequestBody String email) {
        email = email.substring(1, email.length() - 1);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String jwt = jwtUtils.generateJwtTokenGoogle(user.get().getUsername());
            user.get().setVerified(true);
            user.get().setIsActive(Status.ONLINE);
            return ResponseEntity.ok(jwt);
        } else {
            return ResponseEntity.badRequest().body("No Email exists");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.trace("authenticateUser start");
        logger.debug("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new CustomExceptionHandling.InvalidArgumentException("username not found"));

        // Check if the user's account is verified
        if (!user.isVerified()) {
            logger.info("User account is not verified: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Error: Account is not verified."));
        }

        user.setIsActive(Status.ONLINE);
        logger.info("User authenticated successfully: {}", userDetails.getUsername());

        logger.trace("authenticateUser end");
//       loginNotificationService.sendLoginNotification(loginRequest.getUsername());
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
                .community(communityRepository.findByName(
                                Arrays.stream(University.values())
                                        .filter(university -> university.getDomain().equals(signUpRequest.getEmail().split("@")[1]))
                                        .toList()
                                        .getFirst())
                        .orElseThrow(() -> new CustomExceptionHandling.InvalidArgumentException("Email is not valid for a university")))
                .fullname(signUpRequest.getFullname())
                .isActive(Status.ONLINE)
                .build();
        userRepository.save(user);

        logger.info("User registered successfully: {}", signUpRequest.getUsername());

        verificationService.sendVerificationEmail(user);
        logger.trace("registerUser end");
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerificationRequest verificationRequest) {
        boolean isVerified = verificationService.verifyUser(verificationRequest.getUsername(), verificationRequest.getCode());
        if (isVerified) {
            return ResponseEntity.ok(new MessageResponse("User verified successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid verification code."));
        }
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
