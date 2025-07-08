package service;

import model.Product;

import java.util.List;
import java.util.Optional;

public class ProductService implements IProductService{

    @Override
    public Optional<Product> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Product> getList() {
        return List.of();
    }

    @Override
    public List<Product> getListByCategory(String category) {
        return List.of();
    }

    @Override
    public Product createProduct(Product product) {
        return null;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }
}
