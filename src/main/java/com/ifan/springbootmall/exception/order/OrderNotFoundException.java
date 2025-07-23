package com.ifan.springbootmall.exception.order;

public class OrderNotFoundException extends RuntimeException{
    private final Long orderId;

    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }
}