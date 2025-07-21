package com.ifan.springbootmall.exception.user;

public class UserExistException extends RuntimeException{
    public UserExistException() {
        super("User already exist");
    }
}
