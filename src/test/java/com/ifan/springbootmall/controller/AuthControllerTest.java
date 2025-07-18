package com.ifan.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    private User testedUser;
    private User savedUser;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testedUser = new User();
        testedUser.setEmail("<EMAIL>");
        testedUser.setPassword("<PASSWORD>");

        savedUser = new User();
        savedUser.setEmail("<EMAIL>");
        savedUser.setPassword("<PASSWORD>");
        savedUser.setUserId(1L);
    }

    @Test
    void register_WhenEmailNotExistAndUserIsValid_ShouldCreateUser() throws Exception{
        when(userService.isEmailExist(testedUser.getEmail())).thenReturn(false);
        when(userService.createUser(testedUser)).thenReturn(savedUser);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", equalTo(savedUser.getEmail())))
                .andExpect(jsonPath("$.userId", equalTo(savedUser.getUserId())));

        verify(userService).isEmailExist(testedUser.getEmail());
        verify(userService).createUser(testedUser);
    }

    @Test
    void register_WhenEmailExist_ShouldThrowBadRequest() throws Exception{
        when(userService.isEmailExist(testedUser.getEmail())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(userService).isEmailExist(testedUser.getEmail());
        verify(userService, never()).createUser(testedUser);
    }

    @Test
    void login() {
    }
}