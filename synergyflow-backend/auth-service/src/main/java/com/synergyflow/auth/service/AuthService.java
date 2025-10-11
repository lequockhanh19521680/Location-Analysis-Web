package com.synergyflow.auth.service;

import com.synergyflow.auth.dto.*;
import com.synergyflow.auth.entity.User;
import com.synergyflow.auth.mapper.UserMapper;
import com.synergyflow.auth.repository.UserRepository;
import com.synergyflow.common.exception.BadRequestException;
import com.synergyflow.common.exception.ResourceNotFoundException;
import com.synergyflow.common.exception.UnauthorizedException;
import com.synergyflow.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
        
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());
        
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        
        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(user))
                .build();
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());
        
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(user))
                .build();
    }
    
    @Transactional
    public AuthResponse googleOAuth(GoogleOAuthRequest request) {
        // Check if user exists by Google ID
        User user = userRepository.findByGoogleId(request.getGoogleId())
                .orElseGet(() -> {
                    // Check if user exists by email
                    return userRepository.findByEmail(request.getEmail())
                            .map(existingUser -> {
                                // Link Google account to existing user
                                existingUser.setGoogleId(request.getGoogleId());
                                if (request.getAvatarUrl() != null) {
                                    existingUser.setAvatarUrl(request.getAvatarUrl());
                                }
                                return userRepository.save(existingUser);
                            })
                            .orElseGet(() -> {
                                // Create new user
                                User newUser = User.builder()
                                        .email(request.getEmail())
                                        .googleId(request.getGoogleId())
                                        .fullName(request.getFullName())
                                        .avatarUrl(request.getAvatarUrl())
                                        .build();
                                return userRepository.save(newUser);
                            });
                });
        
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());
        
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toDto(user))
                .build();
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid token type. Refresh token required");
        }
        
        // Find user by refresh token
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        
        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), user.getEmail());
        
        user.setRefreshToken(newRefreshToken);
        user = userRepository.save(user);
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(userMapper.toDto(user))
                .build();
    }
    
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toDto(user);
    }
    
    @Transactional
    public UserDto updateProfile(String userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }
        
        if (userDto.getAvatarUrl() != null) {
            user.setAvatarUrl(userDto.getAvatarUrl());
        }
        
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
