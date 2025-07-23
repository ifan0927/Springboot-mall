package com.ifan.springbootmall.exception.order;

public class NullOrderException extends RuntimeException{
    public NullOrderException() {
        super("Order is null");
    }
}