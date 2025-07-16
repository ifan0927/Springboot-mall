package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.PasswordHistory;
import com.ifan.springbootmall.model.Product;
import com.ifan.springbootmall.repository.PasswordHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    private PasswordService passwordService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private List<PasswordHistory> createPasswordHistories(int count, Long userId) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ids.add(i);
        }


        List<PasswordHistory> passwordHistories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setPwdId(ids.get(i).longValue());
            passwordHistory.setUserId(userId);
            passwordHistory.setPwdHash("<PASSWORD>" + i);
            passwordHistory.setCreatedDate(LocalDateTime.now());
            passwordHistories.add(passwordHistory);
        }
        return passwordHistories;
    }

    // Input: plain password, rule: 1 upper 1unique at least 8 charter
    @Test
    void isPasswordValid_WhenPasswordIsValid_ShouldReturnTrue() {
        String password = "<PASSWORD>";

        boolean result = passwordService.isPasswordValid(password);

        assertTrue(result);
    }

    @Test
    void isPasswordValid_WhenPasswordIsTooShort_ShouldReturnFalse() {
        String password = "<Pwd>";

        boolean result = passwordService.isPasswordValid(password);

        assertFalse(result);
    }

    @Test
    void isPasswordValid_WhenPasswordHasNoUpper_ShouldReturnFalse() {
        String password = "<password>";

        boolean result = passwordService.isPasswordValid(password);

        assertFalse(result);
    }

    @Test
    void isPasswordValid_WhenPasswordHasNoUniqueChar_ShouldReturnFalse() {
        String password = "PASSWORD";

        boolean result = passwordService.isPasswordValid(password);

        assertFalse(result);
    }

    @Test
    void hashPassword_WhenInputIsValid_ShouldReturnHashedPassword() {
        String password = "<PASSWORD>";


        String result = passwordService.hashPassword(password);
        assertTrue(passwordEncoder.matches(password, result));
    }

    @Test
    void isPassWordExpired_WhenPasswordIsExpired_ShouldReturnTrue() {
        Long userId = 1L;
        List<PasswordHistory> passwordHistories = createPasswordHistories(1, userId);
        passwordHistories.get(0).setCreatedDate(LocalDateTime.now().minusDays(31));
        when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId)).thenReturn(passwordHistories);

        boolean result = passwordService.isPassWordExpired(userId);

        assertTrue(result);
        verify(passwordHistoryRepository).findByUserIdOrderByCreatedDateDesc(userId);
    }

    @Test
    void isPassWordExpired_WhenPasswordIsNotExpired_ShouldReturnFalse() {
        Long userId = 1L;
        List<PasswordHistory> passwordHistories = createPasswordHistories(2, userId);
        passwordHistories.get(0).setCreatedDate(LocalDateTime.now().minusDays(29));
        passwordHistories.get(1).setCreatedDate(LocalDateTime.now().minusDays(31));
        when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId)).thenReturn(passwordHistories);

        boolean result = passwordService.isPassWordExpired(userId);
        assertFalse(result);
        verify(passwordHistoryRepository).findByUserIdOrderByCreatedDateDesc(userId);
    }


    // Input: HasedPassword
    @Test
    void isPassWordInHistory_WhenPasswordIsInHistory_ShouldReturnTrue() {
        Long userId = 1L;
        List<PasswordHistory> passwordHistories = createPasswordHistories(10, userId);
        for (int i = 0; i < 10; i++) {
            long days = 30L - i;
            passwordHistories.get(i).setCreatedDate(LocalDateTime.now().minusDays(days));
        }
        when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId)).thenReturn(passwordHistories);
        String existingPassword = passwordHistories.get(1).getPwdHash();

        boolean result = passwordService.isPassWordInHistory(userId, existingPassword);

        assertTrue(result);
        verify(passwordHistoryRepository).findByUserIdOrderByCreatedDateDesc(userId);

    }

    @Test
    void isPassWordInHistory_WhenPasswordIsNotInHistory_ShouldReturnFalse() {
        Long userId = 1L;
        List<PasswordHistory> passwordHistories = createPasswordHistories(10, userId);
        for (int i = 0; i < 10; i++) {
            long days = 30L - i;
            passwordHistories.get(i).setCreatedDate(LocalDateTime.now().minusDays(days));
        }
        when(passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId)).thenReturn(passwordHistories);

        boolean result = passwordService.isPassWordInHistory(userId, "not existing password");

        assertFalse(result);
        verify(passwordHistoryRepository).findByUserIdOrderByCreatedDateDesc(userId);
    }
}