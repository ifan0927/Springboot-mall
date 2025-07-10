package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByStockGreaterThan(Integer stock);
    List<Product> findByCategoryAndStockGreaterThan(ProductCategory category, Integer stock);
 }
