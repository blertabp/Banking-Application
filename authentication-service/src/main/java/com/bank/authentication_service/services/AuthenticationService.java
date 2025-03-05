package com.bank.authentication_service.services;

import com.bank.authentication_service.dtos.LoginRequest;
import com.bank.authentication_service.dtos.LoginResponse;
import com.bank.authentication_service.model.User;
import com.bank.authentication_service.repositories.UserRepository;
import com.bank.authentication_service.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    logger.error("User not found: {}", loginRequest.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        logger.info("User {} authenticated successfully. Token generated.", user.getUsername());
        return new LoginResponse(accessToken, "Bearer");
    }

    public String registerUser(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        logger.info("User {} registered successfully.", user.getUsername());
        return "User registered successfully in auth-service.";
    }

    public String deleteUser(String userId) {
        logger.info("Deleting user with ID: {}", userId);
        userRepository.deleteById(Long.parseLong(userId));
        logger.info("User with ID {} deleted successfully.", userId);
        return "User deleted successfully in auth-service.";
    }
}