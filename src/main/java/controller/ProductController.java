package controller;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProductList() {
        return ResponseEntity.ofNullable();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ofNullable();
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductListByCategory(@PathVariable String category) {
        return ResponseEntity.ofNullable();
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Product> createProduct(@PathVariable Long productId,
                                                 @RequestBody Product product) {
        return ResponseEntity.ofNullable();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        return ResponseEntity.ofNullable();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ofNullable();
    }

}
