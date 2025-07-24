package com.ifan.springbootmall.security;

import com.ifan.springbootmall.exception.auth.InvalidTokenFormatException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private  JwtService jwtService;



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
    void getEmailFromToken_WhenTokenIsInvalid_ShouldThrowException() {
         String token = "invalid token";

         Exception exception = assertThrows(InvalidTokenFormatException.class, () -> jwtService.getEmailFromToken(token));
         assertEquals("Invalid token format", exception.getMessage());
     }


}