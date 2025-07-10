package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ifan.springbootmall.service.ProductService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProductList(@RequestParam(required = false) String  category, @RequestParam(required = false) Integer stock ) {
        ProductCategory productCategory = null;
        if (category != null){
            try{
                productCategory = ProductCategory.valueOf(category.toUpperCase());
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        if (stock != null && stock < 0){
            return ResponseEntity.badRequest().build();
        }
        List<Product> productList = productService.getList(productCategory, stock);
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId).orElse(null));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
                                                 @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}
