package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private UserService userService;

    private User testUser;
    private User savedUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("<EMAIL>");
        testUser.setPassword("<PASSWORD>");

        savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setEmail("<EMAIL>");
        savedUser.setPassword("<PASSWORD>");
    }

}