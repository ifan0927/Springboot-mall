package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> getById(Long id);

    Optional<User> getByEmail(String email);

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long id);

    boolean login(Long userId, String password);

    Long isUserExist(String email);


    boolean checkEmailExist(String email);


}
