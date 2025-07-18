package com.ifan.springbootmall.exception.auth;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException() {
        super("Token expired");
    }
}
