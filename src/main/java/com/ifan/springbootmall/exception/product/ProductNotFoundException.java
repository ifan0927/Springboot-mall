package com.ifan.springbootmall.exception.product;

public class ProductNotFoundException extends RuntimeException{
    private final Long productId;
    private final String productName;

    public ProductNotFoundException(Long productId, String productName) {
        super("Product not found: " + productName + "(" + productId + ")");
        this.productId = productId;
        this.productName = productName;
    }
}
