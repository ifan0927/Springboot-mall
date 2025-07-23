package com.ifan.springbootmall.exception.product;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException() {
        super("Not enough stock");
    }
}
