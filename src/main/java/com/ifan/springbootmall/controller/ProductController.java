package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ifan.springbootmall.service.ProductService;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> getProductList(@RequestParam(required = false) String  category,
                                                        @RequestParam(required = false) Integer stock,
                                                        Pageable pageable) {
        // Category 驗證
        ProductCategory productCategory = null;
        Page<Product> productList = null;
        if (category != null){
            try{
                productCategory = ProductCategory.valueOf(category.toUpperCase());
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        // stock 驗證
        if (stock != null && stock < 0){
            return ResponseEntity.badRequest().build();
        }

        // pageable category 驗證
        Sort sort = pageable.getSort();
        if (sort.isSorted()){
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                if (!"price".equals(property) && !"stock".equals(property) && !"productId".equals(property)){
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        try{
            productList = productService.getList(productCategory, stock, pageable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

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
