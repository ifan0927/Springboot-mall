package com.ifan.springbootmall.constant;

public enum ProductCategory {
    FOODS("食物"),
    CLOTHES("衣服"),
    ELECTRONICS("電器"),
    BOOKS("書本"),
    OTHERS("其他");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
