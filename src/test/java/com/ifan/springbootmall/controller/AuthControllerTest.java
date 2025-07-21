package com.ifan.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifan.springbootmall.exception.auth.InCorrectPasswordException;
import com.ifan.springbootmall.exception.auth.InvalidPasswordException;
import com.ifan.springbootmall.exception.common.EmailNotFoundException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.NullUserException;
import com.ifan.springbootmall.exception.user.UserExistException;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private User testedUser;
    private User savedUser;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testedUser = new User();
        testedUser.setEmail("test@gmail.com");
        testedUser.setPassword("<PASSWORD>");

        savedUser = new User();
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("<PASSWORD>");
        savedUser.setUserId(1L);
    }

    @Test
    void register_WhenEmailNotExistAndUserIsValid_ShouldCreateUser() throws Exception{
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());

        verify(userService).createUser(any(User.class));
    }

    @Test
    void register_WhenEmailExist_ShouldThrowBadRequest() throws Exception{
        when(userService.createUser(any(User.class))).thenThrow(new UserExistException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isConflict());

        verify(userService).createUser(any(User.class));
    }

    @Test
    void register_WhenUserIsNull_ShouldThrowBadRequest() throws Exception{
        when(userService.createUser(any(User.class))).thenThrow(new NullUserException());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void register_WhenUserPasswordIsInvalid_ShouldThrowBadRequest() throws Exception{
        testedUser.setPassword("pwd");
        when(userService.createUser(any(User.class))).thenThrow(new InvalidPasswordException());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(User.class));
    }

    @Test
    void register_WhenUserEmailIsInvalid_ShouldThrowBadRequest() throws Exception{
        testedUser.setEmail("not_email");
        when(userService.createUser(any(User.class))).thenThrow(new InvalidEmailFormatException(testedUser.getEmail()));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(User.class));
    }

    @Test
    void login_WhenUserNotValid_ShouldReturnUnAuthorized() throws Exception {
        testedUser.setEmail("not_email");
        when(userService.login(testedUser.getEmail(), testedUser.getPassword())).thenThrow(new InvalidEmailFormatException(testedUser.getEmail()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());

        verify(userService).login(testedUser.getEmail(), testedUser.getPassword());
    }

    @Test
    void login_WhenUserNotExist_ShouldReturnUnAuthorized() throws Exception {
        testedUser.setEmail("not_email");
        when(userService.login(testedUser.getEmail(), testedUser.getPassword())).thenThrow(new EmailNotFoundException(testedUser.getEmail()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());

        verify(userService).login(testedUser.getEmail(), testedUser.getPassword());
    }

    @Test
    void login_WhenUserExistAndPasswordIsCorrect_ShouldReturnToken() throws Exception {
        when(userService.login(testedUser.getEmail(), testedUser.getPassword())).thenReturn("token");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", equalTo("token")));
        verify(userService).login(testedUser.getEmail(), testedUser.getPassword());
    }

    @Test
    void login_WhenUserExistAndPasswordIsIncorrect_ShouldReturnUnAuthorized() throws Exception {
        when(userService.login(any(), any())).thenThrow(new InCorrectPasswordException());


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized());
        verify(userService).login(any(String.class), any(String.class));
    }





}