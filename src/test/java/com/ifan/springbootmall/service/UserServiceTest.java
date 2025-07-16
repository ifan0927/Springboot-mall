package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import com.ifan.springbootmall.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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


    @InjectMocks
    private UserService userService;

    private User testUser;
    private User savedUser;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
       RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(null));

       assertEquals("User can not be null", exception.getMessage());
       verify(userRepository, never()).save(any(User.class));
       verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));
    }

    @Test
    void createUser_WhenPasswordNotValid_ShouldThrowException() {
        User user = new User();
        user.setPassword("<PASSWORD>");
        when(passwordService.isPasswordValid(anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(user));

        assertEquals("Password not valid", exception.getMessage());
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
        exisitingUser.setPassword("<PASSWORD>");

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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, UpdateUser));

        assertEquals("Password not valid", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, UpdateUser));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(passwordHistoryRepository, never()).save(any(PasswordHistory.class));
        verify(passwordService, never()).isPasswordValid(anyString());
        verify(passwordService, never()).hashPassword(anyString());
    }

    @Test
    void deleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }


    @Test
    void login_WhenEmailExistsAndPasswordValid_ShouldReturnTrue() {
        String email = "test@gmail.com";
        String password = "<PASSWORD>";
        User exisitingUser = new User();
        String encodedPassword = passwordEncoder.encode(password);
        exisitingUser.setUserId(1L);
        exisitingUser.setEmail(email);
        exisitingUser.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(exisitingUser));

        boolean result = userService.login(email, password);

        assertTrue(result);
        verify(userRepository, atLeast(1)).findByEmail(email);
    }

    @Test
    void login_WhenEmailExistsAndPasswordNotValid_ShouldReturnFalse() {
        String email = "test@gmail.com";
        String wrongPassword = "<WRONG_PASSWORD>";
        String password = "<PASSWORD>";
        User exisitingUser = new User();
        String encodedPassword = passwordEncoder.encode(password);
        exisitingUser.setUserId(1L);
        exisitingUser.setEmail(email);
        exisitingUser.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(exisitingUser));
        boolean result = userService.login(email, wrongPassword);

        assertFalse(result);
        verify(userRepository, atLeast(1)).findByEmail(email);
    }

    @Test
    void login_WhenEmailNotExists_ShouldThrowException() {
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.login(email, "any password"));

        assertEquals("Email not found", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.isEmailExist(email));

        assertEquals("Email not valid", exception.getMessage());
        verify(userRepository, never()).findByEmail(email);
    }
}