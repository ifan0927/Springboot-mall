package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.exception.product.NotEnoughStockException;
import com.ifan.springbootmall.exception.product.NullProductException;
import com.ifan.springbootmall.exception.product.ProductNotFoundException;
import com.ifan.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.ifan.springbootmall.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
            throw new NullProductException();
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
        throw new ProductNotFoundException(productId);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public int getProductPrice(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            throw new ProductNotFoundException(productId);
        }
        return productOptional.get().getPrice();
    }

    @Override
    public boolean hasEnoughStock(Long productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            throw new ProductNotFoundException(productId);
        }
        return productOptional.get().getStock() >= quantity;
    }

    @Override
    public void decreaseStock(Long productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            throw new ProductNotFoundException(productId);
        }
        Product product = productOptional.get();
        if (product.getStock() < quantity) {
            throw new NotEnoughStockException();
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    public void restoreStock(Long productId, Integer quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            throw new ProductNotFoundException(productId);
        }
        Product product = productOptional.get();
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }



}
