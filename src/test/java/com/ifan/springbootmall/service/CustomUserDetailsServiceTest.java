package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.UserIsDeletedException;
import com.ifan.springbootmall.exception.user.UserNotFoundException;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.model.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @MockitoBean
    private  UserService userService ;

    @Autowired
    private  CustomUserDetailsService service;

    @Test
    void loadUserByUsername_WhenEmailIsValid_AndUserExisitedNotDeleted_ShouldReturnUserPrincipal() {
        String email = "test@gmail.com";
        User user;
        user = new User();
        user.setEmail(email);
        user.setPassword("<PASSWORD>");
        user.setUserId(1L);

        when(userService.getByEmail(email)).thenReturn(java.util.Optional.of(user));

        UserDetails userPrincipal = service.loadUserByUsername(email);

        assertEquals(UserPrincipal.class, userPrincipal.getClass());
        assertEquals(email, userPrincipal.getUsername());

        verify(userService).getByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenEmailIsNotValid_ShouldThrowException() {
        String email = "InvalidEmail";

        when(userService.getByEmail(email)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(InvalidEmailFormatException.class, () -> service.loadUserByUsername(email));
        assertEquals("Invalid email: " + email, exception.getMessage());

    }

    @Test
    void loadUserByUsername_WhenEmailIsNotFound_ShouldThrowException(){
        String email = "test@gmail.com";

        when(userService.getByEmail(email)).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> service.loadUserByUsername(email));
        assertEquals("User not found", exception.getMessage());

        verify(userService).getByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenUserIsDeleted_ShouldThrowException(){
        String email = "test@gmail.com";
        User user;
        user = new User();
        user.setEmail(email);
        user.setUserId(1L);
        user.setDeleted(true);

        when(userService.getByEmail(email)).thenReturn(java.util.Optional.of(user));

        Exception exception = assertThrows(UserIsDeletedException.class, () -> service.loadUserByUsername(email));
        assertEquals("User is deleted" , exception.getMessage());

        verify(userService).getByEmail(email);
    }

}