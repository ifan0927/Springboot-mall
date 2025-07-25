package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.auth.InCorrectPasswordException;
import com.ifan.springbootmall.exception.auth.InvalidPasswordException;
import com.ifan.springbootmall.exception.common.EmailNotFoundException;
import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.NullUserException;
import com.ifan.springbootmall.exception.user.UserNotFoundException;
import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import com.ifan.springbootmall.repository.UserRepository;
import com.ifan.springbootmall.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.awt.dnd.InvalidDnDOperationException;
import java.util.ArrayList;
import java.util.List;
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

    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @Mock
    private JwtService jwtService;


    @InjectMocks
    private UserService userService;

    private User testUser;
    private User savedUser;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("<PASSWORD>");

        savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("<PASSWORD>");
    }


    @Test
    void getById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getById(userId);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        verify(userRepository).findById(userId);
    }

    @Test
    void getById_WhenUserNotExists_ShouldReturnEmptyOptional() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getById(userId);
        assertTrue(result.isEmpty());

        verify(userRepository).findById(userId);
    }

    @Test
    void getByEmail_WhenUserExists_ShouldReturnUser() {
        String email = "<EMAIL>";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void getByEmail_WhenUserNotExists_ShouldReturnEmptyOptional() {
        String email = "<EMAIL>";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userService.getByEmail(email);

        assertTrue(result.isEmpty());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void createUser_WhenUserExists_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordService.isPasswordValid(anyString())).thenReturn(true);
        when(passwordService.hashPassword(anyString())).thenReturn("<PASSWORD>");
        when(passwordHistoryRepository.save(any(PasswordHistory.class))).thenReturn(new PasswordHistory());

        User result = userService.createUser(testUser);

        assertEquals(savedUser, result);
        verify(userRepository).save(any(User.class));
        verify(passwordService).isPasswordValid(anyString());
        verify(passwordService).hashPassword(anyString());
        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
    }

    @Test
    void createUser_WhenUserNotExists_ShouldThrowException() {
       RuntimeException exception = assertThrows(NullUserException.class, () -> userService.createUser(null));

       assertEquals("User is null", exception.getMessage());
       verify(userRepository, never()).save(any(User.class));
       verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));
    }

    @Test
    void createUser_WhenPasswordNotValid_ShouldThrowException() {
        User user = new User();
        user.setPassword("<PASSWORD>");
        when(passwordService.isPasswordValid(anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(InvalidPasswordException.class, () -> userService.createUser(user));

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordService).isPasswordValid(anyString());
        verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));
    }

    @Test
    void updateUser_WhenUserExistsAndNotChangingPassword_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        User exisitingUser = new User();
        exisitingUser.setUserId(userId);
        exisitingUser.setEmail("<EMAIL>");
        exisitingUser.setPassword("$2a$12$J9sBb1wfJ4udtWGz4qh8genzhvU1bty3M49DUzqHCXjyl2XptkW9S");

        User UpdateUser = new User();
        UpdateUser.setUserId(userId);
        UpdateUser.setEmail("<CHANGED_EMAIL>");
        UpdateUser.setPassword("<PASSWORD>");

        User expectedUser = new User();
        expectedUser.setUserId(userId);
        expectedUser.setEmail("<CHANGED_EMAIL>");
        expectedUser.setPassword("<PASSWORD>");

        when(userRepository.findById(userId)).thenReturn(Optional.of(exisitingUser));
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.updateUser(userId, UpdateUser);

        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExistsAndChangingPassword_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        User exisitingUser = new User();
        exisitingUser.setUserId(userId);
        exisitingUser.setEmail("<EMAIL>");
        String hashedOldPassword = passwordEncoder.encode("<PASSWORD>");
        exisitingUser.setPassword(hashedOldPassword);

        User UpdateUser = new User();
        UpdateUser.setUserId(userId);
        UpdateUser.setEmail("<CHANGED_EMAIL>");
        String hashedNewPassword = passwordEncoder.encode("<NEW_PASSWORD>");
        UpdateUser.setPassword(hashedNewPassword);

        User ExpectedUser = new User();
        ExpectedUser.setUserId(userId);
        ExpectedUser.setEmail("<CHANGED_EMAIL>");
        ExpectedUser.setPassword(hashedNewPassword);

        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setPwdHash(hashedNewPassword);
        passwordHistory.setUserId(userId);

        List<PasswordHistory> passwordHistories = new ArrayList<>();
        passwordHistories.add(passwordHistory);

        when(userRepository.findById(userId)).thenReturn(Optional.of(exisitingUser));
        when(passwordService.hashPassword(anyString())).thenReturn(hashedNewPassword);
        when(passwordService.isPasswordValid(anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(ExpectedUser);
        when(passwordHistoryRepository.save(any(PasswordHistory.class))).thenReturn(passwordHistory);
        when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId)).thenReturn(passwordHistories);

        User result = userService.updateUser(userId, UpdateUser);

        PasswordHistory savedPasswordHistory = passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId).get(0);

        assertEquals(ExpectedUser, result);
        assertEquals(hashedNewPassword, savedPasswordHistory.getPwdHash());
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
        verify(passwordService).hashPassword(anyString());
        verify(passwordService).isPasswordValid(anyString());
        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
    }

    @Test
    void updateUser_WhenUserExistsAndPasswordNotValid_ShouldThrowException() {
        Long userId = 1L;
        User exisitingUser = new User();
        exisitingUser.setUserId(userId);
        exisitingUser.setEmail("<EMAIL>");
        exisitingUser.setPassword("<PASSWORD>");

        User UpdateUser = new User();
        UpdateUser.setUserId(userId);
        UpdateUser.setEmail("<CHANGED_EMAIL>");
        UpdateUser.setPassword("<NEW PASSWORD>");

        when(userRepository.findById(userId)).thenReturn(Optional.of(exisitingUser));
        when(passwordService.isPasswordValid(anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(InvalidPasswordException.class, () -> userService.updateUser(userId, UpdateUser));

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(passwordService).isPasswordValid(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));

    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowException() {
        Long userId = 1L;
        User UpdateUser = new User();
        UpdateUser.setUserId(userId);
        UpdateUser.setEmail("<CHANGED_EMAIL>");
        UpdateUser.setPassword("<PASSWORD>");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, UpdateUser));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));
        verify(passwordService, never()).isPasswordValid(anyString());
        verify(passwordService, never()).hashPassword(anyString());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDisableUser() {
        Long userId = 1L;
        User exisitingUser = new User();
        exisitingUser.setUserId(userId);

        User expectedUser = new User();
        expectedUser.setUserId(userId);
        expectedUser.setDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(exisitingUser));
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found" , exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void login_WhenEmailExistsAndPasswordValid_ShouldReturnTrue() {
        String email = "test@gmail.com";
        String password = "<PASSWORD>";

        User exisitingUser = new User();
        exisitingUser.setUserId(1L);
        exisitingUser.setEmail(email);
        exisitingUser.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(exisitingUser));
        when(jwtService.generateToken(email)).thenReturn("test token");

        String result = userService.login(email, password);

        assertEquals("test token", result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void login_WhenEmailExistsAndPasswordNotCorrect_ShouldThrowException() {
        String email = "test@gmail.com";
        String password = "InCorrectPassword";

        User exisitingUser = new User();
        exisitingUser.setUserId(1L);
        exisitingUser.setEmail(email);
        exisitingUser.setPassword(passwordEncoder.encode("<PASSWORD>"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(exisitingUser));

        Exception exception = assertThrows(InCorrectPasswordException.class, () -> userService.login(email, password));
        assertEquals("InCorrect password", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(jwtService, never()).generateToken(anyString());

    }

    @Test
    void login_WhenEmailNotExists_ShouldThrowException() {
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmailNotFoundException.class, () -> userService.login(email, "any password"));

        assertEquals("Email not found: " + email, exception.getMessage());
    }

    @Test
    void login_WhenEmailNotValid_ShouldThrowException() {
        String email = "not a valid email";

        Exception exception = assertThrows(InvalidEmailFormatException.class, () -> userService.login(email, "any password"));

        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(anyString());
        assertEquals("Invalid email: " + email, exception.getMessage());
    }

    @Test
    void isEmailExist_WhenEmailExists_ShouldReturnTrue() {
        String email = "test@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        boolean result = userService.isEmailExist(email);

        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailExist_WhenEmailNotExists_ShouldReturnFalse() {
        String email = "test@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = userService.isEmailExist(email);

        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailExist_WhenEmailNotValid_ShouldThrowException() {
        String email = "not a valid email";

        RuntimeException exception = assertThrows(InvalidEmailFormatException.class, () -> userService.isEmailExist(email));

        assertEquals("Invalid email: " + email, exception.getMessage());
        verify(userRepository, never()).findByEmail(email);
    }
}