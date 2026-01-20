package com.thotran.sochitieu.security;

import com.thotran.sochitieu.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Component xử lý việc tạo và validate JWT token.
 */
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400}") // Default: 24 hours (in seconds)
    private Long jwtExpiration;
    
    /**
     * Tạo JWT token từ User
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Lấy userId từ JWT token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token không hợp lệ hoặc hết hạn
            return false;
        }
    }
    
    /**
     * Lấy thời gian hết hạn (seconds)
     */
    public Long getExpirationInSeconds() {
        return jwtExpiration;
    }
    
    /**
     * Tạo signing key từ secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
