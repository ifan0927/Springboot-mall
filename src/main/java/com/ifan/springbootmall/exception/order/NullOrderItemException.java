package com.ifan.springbootmall.exception.order;

public class NullOrderItemException extends RuntimeException{
    public NullOrderItemException() {
        super("OrderItem is null");
    }
}