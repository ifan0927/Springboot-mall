package com.ifan.springbootmall.exception.user;

public class UserNotFoundException extends RuntimeException{
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found: " + userId);
        this.userId = userId;
    }
}
