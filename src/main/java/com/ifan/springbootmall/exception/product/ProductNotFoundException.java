package com.ifan.springbootmall.exception.product;

public class ProductNotFoundException extends RuntimeException{
    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId);
        this.productId = productId;

    }
}
