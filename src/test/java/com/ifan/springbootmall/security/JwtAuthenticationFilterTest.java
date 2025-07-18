package com.ifan.springbootmall.security;

import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.model.UserPrincipal;
import com.ifan.springbootmall.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void assertNoAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    private void assertAuthenticationSet(String expectedEmail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(expectedEmail, auth.getName());
        assertTrue(auth.isAuthenticated());
    }

    @Test
    void doFilterInternal_WhenValidToken_ShouldSetAuthentication() throws Exception {
        String token = "valid-jwt-token";
        String email = "test@gmail.com";
        String password = "<PASSWORD>";
        User user;
        user = new User();
        user.setEmail(email);
        user.setPassword(password);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getEmailFromToken(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userPrincipal);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertAuthenticationSet(email);

        verify(request).getHeader("Authorization");
        verify(jwtService).getEmailFromToken(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(filterChain).doFilter(request, response);

    }

    @Test
    void doFilterInternal_WhenInvalidHeader_ShouldNotSetAuthentication() throws Exception {
        String token = "jwt-token";

        when(request.getHeader("Authorization")).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNoAuthentication();

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenHeaderNull_ShouldNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNoAuthentication();

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenCatchJwtServiceException_ShouldNotSetAuthentication() throws  Exception {
        String token = "invalid-jwt-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getEmailFromToken(token)).thenThrow(RuntimeException.class);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNoAuthentication();

        verify(request).getHeader("Authorization");
        verify(jwtService).getEmailFromToken(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenCatchUserDetailException_ShouldNotSetAuthentication() throws RuntimeException, Exception {
        String token = "valid.jwt.token";
        String email = "test@gmail.com";
        String password = "<PASSWORD>";
        Long userId = 1L;
        User user;
        user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUserId(userId);


        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.getEmailFromToken(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenThrow(RuntimeException.class);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNoAuthentication();
        verify(request).getHeader("Authorization");
        verify(jwtService, atLeast(1)).getEmailFromToken(token);
        verify(userDetailsService, atLeast(1)).loadUserByUsername(email);

    }


}