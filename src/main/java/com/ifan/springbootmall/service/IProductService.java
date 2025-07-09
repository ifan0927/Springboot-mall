package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Optional<Product> getById(Long id);
    List<Product> getList();
    List<Product> getListByCategory(String category);
    Product createProduct(Product product);
    Product updateProduct(Long productId, Product product);
    void deleteProduct(Long id);
}
