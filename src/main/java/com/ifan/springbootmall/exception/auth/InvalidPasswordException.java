package com.ifan.springbootmall.exception.auth;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException() {
        super("Invalid password");
    }
}
