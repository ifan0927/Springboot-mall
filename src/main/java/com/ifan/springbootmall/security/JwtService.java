package com.ifan.springbootmall.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public String generateToken(String email) {
        return null;
    }

    public String getEmailFromToken(String token) {
        return null;
    }

    public boolean isTokenValid(String token) {
        return false;
    }

    public boolean isTokenExpired(String token) {
        return false;
    }
}
