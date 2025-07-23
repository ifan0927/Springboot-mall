package com.ifan.springbootmall.repository;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Product> findByCategory(@NonNull ProductCategory category, @NonNull Pageable pageable);

    @NonNull
    Page<Product> findByStockGreaterThan(@NonNull Integer stock, @NonNull Pageable pageable);

    @NonNull
    Page<Product> findByCategoryAndStockGreaterThan(@NonNull ProductCategory category, @NonNull Integer stock,@NonNull Pageable pageable);
 }
