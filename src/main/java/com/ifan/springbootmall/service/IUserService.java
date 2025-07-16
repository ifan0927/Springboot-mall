package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> getById(Long id);

    Optional<User> getByEmail(String email);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long id);

    boolean login(String email, String password);

    boolean isEmailExist(String email);


}
