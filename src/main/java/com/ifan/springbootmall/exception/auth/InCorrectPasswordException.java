package com.ifan.springbootmall.exception.auth;

public class InCorrectPasswordException extends RuntimeException{
    public InCorrectPasswordException() {
        super("InCorrect password");
    }
}
