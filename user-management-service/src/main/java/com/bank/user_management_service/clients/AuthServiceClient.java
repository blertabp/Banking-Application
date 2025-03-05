package com.bank.user_management_service.clients;

import com.bank.user_management_service.dto.AuthUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8080/auth")
public interface AuthServiceClient {


    @PostMapping("/register")
    void registerUser(@RequestBody AuthUserRequest request);

    @DeleteMapping("/deleteUser/{userId}")
    void deleteUser(@PathVariable Long userId);
}