package com.ifan.springbootmall.service;

import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ifan.springbootmall.repository.ProductRepository;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
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
    }

    private List<Product> createTestProducts(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ids.add(i);
        }

        Collections.shuffle(ids);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setProductId(ids.get(i).longValue());
            product.setProductName("Product " + ids.get(i));
            product.setPrice(i * 100);
            product.setStock(i * 5);
            product.setCategory(ProductCategory.ELECTRONICS);
            products.add(product);
        }
        return products;
    }

    @Test
    void getById_WhenProductExists_ShouldReturnProduct() {
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
    void getById_WhenProductNotExists_ShouldReturnEmptyOptional() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getById(productId);
        assertTrue(result.isEmpty());

        verify( productRepository ).findById(productId);
    }

    @Test
    void getList_WithPagination_ShouldReturnPagedResult(){
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(30);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 30);

        when(productRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Product> result = productService.getList(null, null, pageable);

        assertEquals(mockPage, result);
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getList_WithCategory_ShouldReturnPagedResult(){
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productRepository.findByCategory(ProductCategory.ELECTRONICS, pageable)).thenReturn(mockPage);

        Page<Product> result = productService.getList(ProductCategory.ELECTRONICS, null, pageable);

        assertEquals(mockPage, result);
        verify(productRepository).findByCategory(ProductCategory.ELECTRONICS, pageable);
    }

    @Test
    void getList_WithStockMoreThan_ShouldReturnPagedResult(){
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productRepository.findByStockGreaterThan(5, pageable)).thenReturn(mockPage);

        Page<Product> result = productService.getList(null, 5, pageable);

        assertEquals(mockPage, result);
        verify(productRepository).findByStockGreaterThan(5, pageable);
    }

    @Test
    void getList_WithCategoryAndStockMoreThan_ShouldReturnPagedResult(){
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productRepository.findByCategoryAndStockGreaterThan(ProductCategory.ELECTRONICS, 5, pageable)).thenReturn(mockPage);

        Page<Product> result = productService.getList(ProductCategory.ELECTRONICS, 5, pageable);

        assertEquals(mockPage, result);
        verify(productRepository).findByCategoryAndStockGreaterThan(ProductCategory.ELECTRONICS, 5, pageable);
    }



    @Test
    void createProduct_WhenProductNotExists_ShouldReturnSavedProduct() {
        Product product = savedProduct;
        when(productRepository.save(product)).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertEquals(savedProduct, result);
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_withNull_WhenProductNotExists_ShouldThrowException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.createProduct(null));

        assertEquals("Product can not be null", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenProductExistedAndIsUpdated_ShouldReturnUpdatedProduct() {
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
