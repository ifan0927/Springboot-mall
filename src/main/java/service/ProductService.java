package service;

import model.Product;

import java.util.List;

public class ProductService implements IProductService{

    @Override
    public Product getById(Long id) {
        return null;
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
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }
}
