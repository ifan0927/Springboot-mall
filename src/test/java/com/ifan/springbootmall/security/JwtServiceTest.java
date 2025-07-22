package com.ifan.springbootmall.security;

import com.ifan.springbootmall.exception.auth.InvalidTokenFormatException;
import com.ifan.springbootmall.exception.auth.TokenExpiredException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.security.Key;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private  JwtService jwtService;

     private Key getSignInKey() {
        // 測試用的固定 secret
        byte[] keyBytes = Decoders.BASE64.decode("myJwtSecretKeyForPracticeProjectShouldBeLongEnoughToPassSomething");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String createToken(String email, Long expirationTimeMillis) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void generateToken_WhenEmailIsValid_ShouldReturnToken() {
        String email = "test@gmail.com";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test void generateToken_WhenEmailIsInvalid_ShouldThrowException() {
        String email = "InvalidEmail";

        Exception exception = assertThrows(InvalidEmailFormatException.class, () -> jwtService.generateToken(email));
        assertEquals("Invalid email: " + email , exception.getMessage());
    }

    @Test
    void getEmailFromToken_WhenTokenIsValid_ShouldReturnEmail() {
        String email = "test@gmail.com";
        String token = createToken(email, 1800000L);
        String result = jwtService.getEmailFromToken(token);
        assertEquals(email, result);
    }

    @Test
    void getEmailFromToken_WhenTokenIsInvalid_ShouldThrowException() {
         String token = "invalid token";

         Exception exception = assertThrows(InvalidTokenFormatException.class, () -> jwtService.getEmailFromToken(token));
         assertEquals("Invalid token format", exception.getMessage());
     }

    @Test
    void getEmailFromToken_WhenTokenExpired_ShouldThrowException() {
         String email = "test@gmail.com";
         String token = createToken(email , -1800000L);
         String fullToken = "Bearer " + token;

         Exception exception = assertThrows(TokenExpiredException.class, () -> jwtService.getEmailFromToken(fullToken));
         assertEquals("Token expired", exception.getMessage());

    }

}