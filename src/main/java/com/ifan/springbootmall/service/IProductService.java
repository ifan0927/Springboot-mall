package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Optional<Product> getById(Long id);
    List<Product> getList(Optional<ProductCategory> category, Optional<Integer> stock);
    Product createProduct(Product product);
    Product updateProduct(Long productId, Product product);
    void deleteProduct(Long id);
}
