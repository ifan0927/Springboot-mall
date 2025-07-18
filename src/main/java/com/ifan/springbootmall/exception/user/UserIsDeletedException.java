package com.ifan.springbootmall.exception.user;

public class UserIsDeletedException extends RuntimeException{

    public UserIsDeletedException() {
        super("User is deleted" );

    }
}
