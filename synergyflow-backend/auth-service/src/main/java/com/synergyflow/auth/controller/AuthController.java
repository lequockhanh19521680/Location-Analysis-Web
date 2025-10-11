package com.synergyflow.auth.controller;

import com.synergyflow.auth.dto.*;
import com.synergyflow.auth.service.AuthService;
import com.synergyflow.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/oauth/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleOAuth(@Valid @RequestBody GoogleOAuthRequest request) {
        AuthResponse response = authService.googleOAuth(request);
        return ResponseEntity.ok(ApiResponse.success("OAuth authentication successful", response));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable String userId) {
        UserDto user = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = authService.updateProfile(userId, userDto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }
}
