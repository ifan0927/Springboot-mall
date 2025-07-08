package service;

import model.Product;

import java.util.List;

public interface IProductService {
    List<Product> listAll();
    Product getById(Long id);
    Product getList();
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
}
