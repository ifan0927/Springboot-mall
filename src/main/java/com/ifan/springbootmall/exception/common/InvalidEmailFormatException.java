package com.ifan.springbootmall.exception.common;

public class InvalidEmailFormatException extends RuntimeException{
    private final String email;

    public InvalidEmailFormatException(String email) {
        super("Invalid email: " + email );
        this.email = email;
    }
}
