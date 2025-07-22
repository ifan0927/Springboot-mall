package com.ifan.springbootmall.exception.user;

public class PasswordInHistoryException extends RuntimeException{
    public PasswordInHistoryException() {
        super("Password in history");
    }
}
