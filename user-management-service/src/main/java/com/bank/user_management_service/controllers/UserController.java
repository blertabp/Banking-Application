package com.bank.user_management_service.controllers;

import com.bank.user_management_service.model.User;
import com.bank.user_management_service.services.UserService;
import com.bank.user_management_service.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }



    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        User user = userService.getUserById(jwtUtil.extractUserId(token.substring(7)));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me/update")
    public ResponseEntity<User> updateCurrentUser(@RequestHeader("Authorization") String token,@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(jwtUtil.extractUserId(token.substring(7)), user));
    }
}
