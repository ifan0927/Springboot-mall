package com.ifan.springbootmall.exception.auth;

public class InvalidTokenFormatException extends RuntimeException{
    public InvalidTokenFormatException() {
        super("Invalid token format");
    }
}
