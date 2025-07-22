package com.ifan.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifan.springbootmall.exception.auth.InCorrectPasswordException;
import com.ifan.springbootmall.exception.auth.InvalidPasswordException;
import com.ifan.springbootmall.exception.common.EmailNotFoundException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.NullUserException;
import com.ifan.springbootmall.exception.user.PasswordInHistoryException;
import com.ifan.springbootmall.exception.user.UserExistException;
import com.ifan.springbootmall.exception.user.UserNotFoundException;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.security.JwtService;
import com.ifan.springbootmall.service.CustomUserDetailsService;
import com.ifan.springbootmall.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
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

    private String fullToken;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("test@gmail.com");
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(customUserDetailsService.loadUserByUsername("test@gmail.com"))
            .thenReturn(mockUserDetails);

        when(jwtService.getEmailFromToken(anyString())).thenReturn("test@gmail.com");
        String token = jwtService.getEmailFromToken("test@gmail.com");
        fullToken = "Bearer " + token;

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

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updatePassword_WhenUserNotExist_ShouldReturnBadRequest() throws Exception{

        when(userService.updateUser(any(Long.class),any(User.class))).thenThrow(new UserNotFoundException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updatePassword_WhenUserExistAndPasswordIsCorrect_ShouldReturnOk() throws Exception{
        savedUser.setUserId(1L);
        when(userService.getByEmail("test@gmail.com")).thenReturn(Optional.of(savedUser));
        when(userService.updateUser(any(Long.class),any(User.class))).thenReturn(savedUser);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", fullToken)
                .content(objectMapper.writeValueAsString(savedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        verify(userService).updateUser(any(Long.class),any(User.class));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updatePassword_WhenUserExistAndPasswordIsNotValid_ShouldReturnBadRequest() throws Exception{
        when(userService.updateUser(any(Long.class),any(User.class))).thenThrow(new InvalidPasswordException());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());


    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updatePassword_WhenUserExistAndPasswordIsInLastThreeHistory_ShouldThrowBadRequest() throws Exception{
        when(userService.updateUser(any(Long.class), any(User.class))).thenThrow(new PasswordInHistoryException());
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedUser));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());


    }





}