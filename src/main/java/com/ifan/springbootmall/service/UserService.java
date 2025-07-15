package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.User;

import java.util.Optional;

public class UserService implements IUserService{
    @Override
    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(Long userId, User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {
    }

    @Override
    public Long isUserExist(String email) {
        return null;
    }

    @Override
    public boolean login(Long userId, String password) {
        return false;
    }

    @Override
    public boolean checkEmailExist(String email) {
        return false;
    }

}
