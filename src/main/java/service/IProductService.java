package service;

import model.Product;

import java.util.List;

public interface IProductService {
    Product getById(Long id);
    List<Product> getList();
    List<Product> getListByCategory(String category);
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
}
