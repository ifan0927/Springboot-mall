package com.ifan.springbootmall.exception.product;

public class NullProductException extends RuntimeException{
    public NullProductException() {
        super("Product is null");
    }
}
