package com.bank.authentication_service.controllers;

import com.bank.authentication_service.dtos.LoginRequest;
import com.bank.authentication_service.dtos.LoginResponse;
import com.bank.authentication_service.model.User;
import com.bank.authentication_service.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {


    private final AuthenticationService authService;


    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
      return ResponseEntity.ok(authService.registerUser(user));
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return ResponseEntity.ok(authService.deleteUser(userId));
    }


}
