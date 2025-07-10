package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.ifan.springbootmall.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Component
public class ProductService implements IProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Page<Product> getList(ProductCategory category, Integer stock, Pageable pageable) {
        if (category == null && stock == null ){
            return productRepository.findAll(pageable);
        }
        if (category != null && stock != null){
            return productRepository.findByCategoryAndStockGreaterThan(category, stock, pageable);
        } else if (category != null) {
            return productRepository.findByCategory(category, pageable);
        } else {
            return productRepository.findByStockGreaterThan(stock, pageable);
        }
    }

    @Override
    public Product createProduct(Product product) {
        if(product == null){
            throw new RuntimeException("Product can not be null");
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        Optional<Product> productUpdate= productRepository.findById(productId);

        if(productUpdate.isPresent()){
            product.setProductId(productId);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found");
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
