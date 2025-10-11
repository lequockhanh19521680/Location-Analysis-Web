package com.synergyflow.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:synergyflow-secret-key-minimum-256-bits-required-for-hs256-algorithm}")
    private String jwtSecret;
    
    @Value("${jwt.access-token-expiration:3600000}") // 1 hour
    private long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration:604800000}") // 7 days
    private long refreshTokenExpiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String generateAccessToken(String userId, String email) {
        return generateToken(userId, email, accessTokenExpiration, "ACCESS");
    }
    
    public String generateRefreshToken(String userId, String email) {
        return generateToken(userId, email, refreshTokenExpiration, "REFRESH");
    }
    
    private String generateToken(String userId, String email, long expiration, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", tokenType);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("email", String.class);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isAccessToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return "ACCESS".equals(claims.get("tokenType", String.class));
    }
    
    public boolean isRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return "REFRESH".equals(claims.get("tokenType", String.class));
    }
}
