package com.ifan.springbootmall.exception.common;

public class EmailNotFoundException extends RuntimeException{
    private final String email;

    public EmailNotFoundException(String email) {
        super("Email not found: " + email);
        this.email = email;
    }
}
