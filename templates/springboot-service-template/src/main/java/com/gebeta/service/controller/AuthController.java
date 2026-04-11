package com.gebeta.service.controller;

import com.gebeta.service.dto.LoginRequest;
import com.gebeta.service.dto.UserResponse;
import com.gebeta.service.model.User;
import com.gebeta.service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody LoginRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), null);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getFullName()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        Map<String, String> response = new HashMap<>();
        response.put("access_token", token);
        response.put("token_type", "bearer");
        return ResponseEntity.ok(response);
    }
}
