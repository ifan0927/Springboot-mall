package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import com.ifan.springbootmall.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ifan.springbootmall.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Product savedProduct;
    private Product lessThanProduct;
    private Product differentProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductName( "test product");
        testProduct.setCategory(ProductCategory.BOOKS);
        testProduct.setImageUrl( "test image url");
        testProduct.setPrice( 100);
        testProduct.setStock( 10);
        testProduct.setDescription( "test description");

        savedProduct = new Product();
        savedProduct.setProductId( 1L);
        savedProduct.setProductName( "test product");
        savedProduct.setCategory(ProductCategory.BOOKS);
        savedProduct.setImageUrl( "test image url");
        savedProduct.setPrice( 100);
        savedProduct.setStock( 10);
        savedProduct.setDescription( "test description");

        lessThanProduct = new Product();
        lessThanProduct.setProductId( 1L);
        lessThanProduct.setProductName( "test product");
        lessThanProduct.setCategory(ProductCategory.BOOKS);
        lessThanProduct.setImageUrl( "test image url");
        lessThanProduct.setPrice( 100);
        lessThanProduct.setStock( 5);
        lessThanProduct.setDescription( "test description");

        differentProduct = new Product();
        differentProduct.setProductId( 1L);
        differentProduct.setProductName( "test product");
        differentProduct.setCategory(ProductCategory.FOODS);
        differentProduct.setImageUrl( "test image url");
        differentProduct.setPrice( 100);
        differentProduct.setStock( 10);
        differentProduct.setDescription( "test description");
    }

    @Test
    void getById() {
        //GIVEN 階段 設定Mock
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        //WHEN 執行測試方法 此處為測試getById 此方法
        Optional<Product> result = productService.getById(productId);

        //THEN 測試部分
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());

        verify( productRepository ).findById(productId);
    }

    @Test
    void getById_notFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getById(productId);
        assertTrue(result.isEmpty());

        verify( productRepository ).findById(productId);
    }

    @Test
    void getList() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        List<Product> result = productService.getList(Optional.empty(), Optional.empty());
        assertEquals(1, result.size());
        assertEquals(testProduct.getProductName(), result.get(0).getProductName());
        assertEquals(testProduct.getProductId(), result.get(0).getProductId());

        verify(productRepository).findAll();
    }

    @Test
    void getListByCategory() {
        ProductCategory category = ProductCategory.BOOKS;
        when(productRepository.findByCategory(category)).thenReturn(List.of(testProduct));

        List<Product> result;
        result = productService.getList(Optional.of(ProductCategory.BOOKS), Optional.empty());

        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));

        verify(productRepository).findByCategory(category);
    }

    @Test
    void getListByStockMoreThan(){
        int stock = 10;

        when(productRepository.findAll()).thenReturn(List.of(savedProduct, lessThanProduct));

        List<Product> result;
        result = productService.getList(Optional.empty(), Optional.of(stock) );

        assertEquals(1, result.size());
        assertEquals(savedProduct, result.get(0));
        assertTrue(result.get(0).getStock() >= stock);
        verify(productRepository).findAll();
    }

    @Test
    void getListByStockMoreThanAndCategory(){
        int stock = 10;
        lessThanProduct.setCategory(ProductCategory.FOODS);
        when(productRepository.findByCategory(ProductCategory.FOODS)).thenReturn(List.of(lessThanProduct, differentProduct));

        List<Product> result;
        result = productService.getList(Optional.of(ProductCategory.FOODS), Optional.of(stock));

        assertEquals(1, result.size());
        assertEquals(differentProduct, result.get(0));
        assertEquals(ProductCategory.FOODS, result.get(0).getCategory());
        assertTrue(result.get(0).getStock() >= stock);
        verify(productRepository).findByCategory(ProductCategory.FOODS);
    }

    @Test
    void createProduct() {
        Product product = savedProduct;
        when(productRepository.save(product)).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertEquals(savedProduct, result);
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_withNull(){
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(null));

        assertEquals("Product can not be null", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setProductName("existingProduct name");
        existingProduct.setPrice(100);

        Product updateData = new Product();
        updateData.setProductName("updatedProduct name");
        updateData.setPrice(1000);

        Product expectedResult;
        expectedResult = new Product();
        expectedResult.setProductId(productId);
        expectedResult.setProductName("updatedProduct name");
        expectedResult.setPrice(1000);


        // Mock：假設資料庫中找得到現有產品
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        // Mock：假設儲存後回傳更新的產品
        when(productRepository.save(any(Product.class))).thenReturn(updateData);

        // When - 執行更新
        Product result = productService.updateProduct(productId, updateData);

        assertEquals("updatedProduct name", result.getProductName());
        assertEquals(1000, result.getPrice());
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));

    }

    @Test
    void updateProduct_WhenProductNotExists_ShouldThrowException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.updateProduct(productId, testProduct));

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDelete() {
        // Given
        Long productId = 1L;

        // When
        productService.deleteProduct(productId);

        // Then - 只驗證 delete 行為
        verify(productRepository).deleteById(productId);
    }

}
