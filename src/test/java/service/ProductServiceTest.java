package service;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        testProduct.setCategory( "test category");
        testProduct.setImageUrl( "test image url");
        testProduct.setPrice( 100);
        testProduct.setStock( 10);
        testProduct.setDescription( "test description");

        savedProduct = new Product();
        savedProduct.setProductId( 1L);
        savedProduct.setProductName( "test product");
        savedProduct.setCategory( "test category");
        savedProduct.setImageUrl( "test image url");
        savedProduct.setPrice( 100);
        savedProduct.setStock( 10);
        savedProduct.setDescription( "test description");
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

        List<Product> result = productService.getList();
        assertEquals(1, result.size());
        assertEquals(testProduct.getProductName(), result.get(0).getProductName());
        assertEquals(testProduct.getProductId(), result.get(0).getProductId());

        verify(productRepository).findAll();
    }

    @Test
    void getListByCategory() {
        String category = "test category";
        when(productRepository.findByCategory(category)).thenReturn(List.of(testProduct));

        List<Product> result = productService.getListByCategory(category);

        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));

        verify(productRepository).findByCategory(category);
    }

    @Test
    void getListByCategory_notFound(){
        String category = "empty category";

        when(productRepository.findByCategory(category)).thenReturn(List.of());

        List<Product> result = productService.getListByCategory(category);

        assertTrue(result.isEmpty());
        verify(productRepository).findByCategory(category);
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
    void updateProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setProductName("existingProduct name");
        existingProduct.setPrice(100);

        Product updateData = new Product();
        updateData.setProductName("updatedProduct name");
        updateData.setPrice(1000);

        Product expectedResult = new Product();
        expectedResult.setProductId(productId);
        expectedResult.setProductName("updatedProduct name");
        expectedResult.setPrice(1000);


        // Mock：假設資料庫中找得到現有產品
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        // Mock：假設儲存後回傳更新的產品
        when(productRepository.save(any(Product.class))).thenReturn(updateData);

        // When - 執行更新
        Product result = productService.updateProduct(productId, updateData);

        assertEquals("updatedProduct name", expectedResult.getProductName());
        assertEquals(1000, expectedResult.getPrice());
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));

    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDelete() {
        // Given
        Long productId = 1L;
        // Mock：假設產品存在
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        productService.deleteProduct(productId);

        // Then - 只驗證 delete 行為
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

}
