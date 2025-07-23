package com.ifan.springbootmall.exception.order;

public class OrderItemNotFoundException extends RuntimeException{
    private final Long orderItemId;

    public OrderItemNotFoundException(Long orderItemId) {
        super("OrderItem not found: " + orderItemId);
        this.orderItemId = orderItemId;
    }
}
