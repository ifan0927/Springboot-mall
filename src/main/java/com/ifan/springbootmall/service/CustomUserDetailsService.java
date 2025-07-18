package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.common.InvalidEmailFormatException;
import com.ifan.springbootmall.exception.user.UserIsDeletedException;
import com.ifan.springbootmall.exception.user.UserNotFoundException;
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
            throw new InvalidEmailFormatException(username);
        }

        Optional<User> user = userService.getByEmail(username);
        if (user.isEmpty()){
            throw new UserNotFoundException();
        }
        User exisitingUser = user.get();
        if (exisitingUser.isDeleted()){
            throw new UserIsDeletedException();
        }
        return new UserPrincipal(exisitingUser);
    }
}
