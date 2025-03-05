package com.bank.user_management_service.services;

import com.bank.user_management_service.clients.AuthServiceClient;
import com.bank.user_management_service.dto.AuthUserRequest;
import com.bank.user_management_service.dto.UserRequestDTO;
import com.bank.user_management_service.model.Role;
import com.bank.user_management_service.model.Status;
import com.bank.user_management_service.model.User;
import com.bank.user_management_service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;

    public UserService(UserRepository userRepository, AuthServiceClient authServiceClient) {
        this.userRepository = userRepository;
        this.authServiceClient = authServiceClient;
    }

    /**
     * Creates a new user and registers it in the auth-service.
     */
    public User createUser(UserRequestDTO userRequest) {
        log.info("Creating new user: {}", userRequest.getUsername());

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setRole(Role.valueOf(userRequest.getRole()));

        User savedUser = userRepository.save(user);
        log.info("User {} saved successfully with ID: {}", savedUser.getUsername(), savedUser.getId());

        // Send credentials to `auth-service`
        AuthUserRequest authUserRequest = new AuthUserRequest(
                savedUser.getId(),
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getRole()
        );
        log.info("Sending user credentials to auth-service for registration");
        authServiceClient.registerUser(authUserRequest);

        return savedUser;
    }

    /**
     * Updates an existing user.
     */
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new RuntimeException("User not found");
                });

        user.setEmail(updatedUser.getEmail());
        user.setUsername(updatedUser.getUsername());

        User updated = userRepository.save(user);
        log.info("User {} updated successfully", updated.getUsername());

        return updated;
    }

    /**
     * Deactivates a user and deletes credentials from the auth-service.
     */
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new RuntimeException("User not found");
                });

        user.deactivate();
        userRepository.save(user);
        log.info("User {} deactivated successfully", user.getUsername());

        log.info("Deleting user credentials from auth-service");
        authServiceClient.deleteUser(id);
    }

    /**
     * Retrieves all active users by role.
     */
    public List<User> getAllUsersByRole(Role role) {
        log.info("Fetching all users with role: {}", role);
        List<User> users = userRepository.findByRoleAndStatus(role, Status.ACTIVE);
        log.info("Found {} active users with role: {}", users.size(), role);
        return users;
    }

    /**
     * Retrieves a user by ID.
     */
    public User getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new RuntimeException("User not found");
                });
    }
}
