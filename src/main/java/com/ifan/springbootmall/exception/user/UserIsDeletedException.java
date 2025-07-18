package com.ifan.springbootmall.exception.user;

public class UserIsDeletedException extends RuntimeException{
    private final Long userId;

    public UserIsDeletedException(Long userId) {
        super("User is deleted: " + userId);
        this.userId = userId;
    }
}
