package com.bank.user_management_service.controllers;

import com.bank.user_management_service.dto.UserRequestDTO;
import com.bank.user_management_service.model.Role;
import com.bank.user_management_service.model.User;
import com.bank.user_management_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {


    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/create-banker")
    public ResponseEntity<User> createBanker(@RequestBody UserRequestDTO user) {
        user.setRole(String.valueOf(Role.BANKER));
        return ResponseEntity.ok(userService.createUser(user));
    }


    @PutMapping("/update-banker/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }


    @DeleteMapping("/delete-banker/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }


    @GetMapping("/bankers")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsersByRole(Role.BANKER));
    }
}
