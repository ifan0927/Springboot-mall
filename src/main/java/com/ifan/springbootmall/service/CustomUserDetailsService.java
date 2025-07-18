package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws RuntimeException {

        if (!username.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            throw new RuntimeException("Invalid email format: " + username);
        }

        Optional<User> user = userService.getByEmail(username);
        if (user.isEmpty()){
            throw new RuntimeException("User not found: " + username);
        }
        User exisitingUser = user.get();
        if (exisitingUser.isDeleted()){
            throw new RuntimeException("User is deleted: " + username);
        }
        return new UserPrincipal(exisitingUser);
    }
}
