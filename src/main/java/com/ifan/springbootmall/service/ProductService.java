package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getList(ProductCategory category, Integer stock) {
        if (category == null && stock == null ){
            return productRepository.findAll();
        }
        if (category != null && stock != null){
            return productRepository.findByCategoryAndStockGreaterThan(category, stock);
        } else if (category != null) {
            return productRepository.findByCategory(category);
        } else {
            return productRepository.findByStockGreaterThan(stock);
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
