package com.ifan.springbootmall.exception.user;

public class NullUserException extends RuntimeException{
    public NullUserException() {
        super("User is null");
    }
}
