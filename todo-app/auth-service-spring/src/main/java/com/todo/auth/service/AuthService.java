package com.todo.auth.service;

import com.todo.auth.dto.AuthResponse;
import com.todo.auth.dto.LoginRequest;
import com.todo.auth.dto.RegisterRequest;
import com.todo.auth.entity.User;
import com.todo.auth.repository.UserDao;
import com.todo.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userDao.selectByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        if (userDao.selectByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setRole("USER");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userDao.insert(user);
        
        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find user by email or username
        User user = userDao.selectByEmail(request.getEmailOrUsername())
                .or(() -> userDao.selectByUsername(request.getEmailOrUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        // Check password
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Check if user is enabled
        if (!user.getEnabled()) {
            throw new RuntimeException("Account is disabled");
        }
        
        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    
    public User validateToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userDao.selectById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
