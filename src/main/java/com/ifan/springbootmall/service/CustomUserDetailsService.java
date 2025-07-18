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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 1. 使用 userService.getByEmail(email) 找到 User
        // TODO: 2. 檢查用戶是否存在
        // TODO: 3. 檢查用戶是否被軟刪除（可選）
        // TODO: 4. 用 UserPrincipal 包裝 User
        // TODO: 5. 返回 UserPrincipal（作為 UserDetails）

        if (!username.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
            throw new UsernameNotFoundException("Invalid email format: " + username);
        }

        Optional<User> user = userService.getByEmail(username);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User not found: " + username);
        }
        User exisitingUser = user.get();
        if (exisitingUser.isDeleted()){
            throw new UsernameNotFoundException("User is deleted: " + username);
        }
        return new UserPrincipal(exisitingUser);
    }
}
