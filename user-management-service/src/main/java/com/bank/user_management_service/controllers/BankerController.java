package com.bank.user_management_service.controllers;

import com.bank.user_management_service.dto.UserRequestDTO;
import com.bank.user_management_service.model.Role;
import com.bank.user_management_service.model.User;
import com.bank.user_management_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/banker")
public class BankerController {


    private final UserService userService;

    public BankerController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create-client")
    public ResponseEntity<User> createClient(@RequestBody UserRequestDTO user) {
        user.setRole(String.valueOf(Role.CLIENT));
        return ResponseEntity.ok(userService.createUser(user));
    }


    @PutMapping("/update-client/{id}")
    public ResponseEntity<User> updateClient(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }


    @DeleteMapping("/delete-client/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("Client deleted successfully");
    }


    @GetMapping("/clients")
    public ResponseEntity<List<User>> getClients() {
        return ResponseEntity.ok(userService.getAllUsersByRole(Role.CLIENT));
    }
}
