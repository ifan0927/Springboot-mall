package service;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import repository.ProductRepository;

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
    public List<Product> getList() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getListByCategory(String category) {
        return productRepository.findByCategory( category);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        Optional<Product> productUpdate= productRepository.findById(productId);

        if(productUpdate.isPresent()){
            product.setProductId(productId);
            return productRepository.save(product);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);

    }
}
