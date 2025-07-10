package com.ifan.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifan.springbootmall.constant.ProductCategory;
import com.ifan.springbootmall.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ifan.springbootmall.service.ProductService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product testedProduct;
    private Product savedProduct;

    @BeforeEach
    void setUp(){
        testedProduct = new Product();
        testedProduct.setProductName("test product");
        testedProduct.setCategory(ProductCategory.BOOKS);
        testedProduct.setImageUrl("test image url");
        testedProduct.setPrice(100);
        testedProduct.setStock(10);
        testedProduct.setDescription("test description");

        savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductName("test product");
        savedProduct.setCategory(ProductCategory.BOOKS);
        savedProduct.setImageUrl("test image url");
        savedProduct.setPrice(100);
        savedProduct.setStock(10);
        savedProduct.setDescription("test description");
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
    void getProductList() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(30);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 30);
        when(productService.getList(null, null, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content.length()", equalTo(30)))
                .andExpect(jsonPath("$.totalElements", equalTo(30)))
                .andExpect(jsonPath("$.last", equalTo(false)));
        verify(productService).getList(null, null, pageable);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProductListWithCategory_ShouldTransformToUpperCase() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productService.getList(ProductCategory.ELECTRONICS, null, pageable)).thenReturn(mockPage);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("category", "electronics")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content.length()", equalTo(20)))
                .andExpect(jsonPath("$.totalElements", equalTo(20)))
                .andExpect(jsonPath("$.last", equalTo(false)));
        verify(productService).getList(ProductCategory.ELECTRONICS, null, pageable);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProductListWithCategory_WhenCategoryNotExists_ShouldThrowBadRequest() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = new ArrayList<>();
        Page<Product> mockPage = new PageImpl<>(products, pageable, 0);
        when(productService.getList(ProductCategory.ELECTRONICS, null, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("category", "not_exists")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(productService, never()).getList(ProductCategory.ELECTRONICS, null, pageable);
    }

    @Test
    void getProductListWithStockMoreThan() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productService.getList(null, 5, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("stock", "5")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content.length()", equalTo(20)))
                .andExpect(jsonPath("$.totalElements", equalTo(20)))
                .andExpect(jsonPath("$.last", equalTo(false)));

        verify(productService).getList(null, 5, pageable);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProductListWithStockMoreThan_WhenStockNotInteger_ShouldThrowBadRequest() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = new ArrayList<>();
        Page<Product> mockPage = new PageImpl<>(products, pageable, 0);
        when(productService.getList(null, 5, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("stock", "not_integer")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(productService, never()).getList(null, 5, pageable);
    }

    @Test
    void getProductListWithStockMoreThan_WhenStockLessThan0_ShouldThrowBadRequest() throws Exception{
        Pageable pageable = PageRequest.of(0,10);
        List<Product> products = new ArrayList<>();
        Page<Product> mockPage = new PageImpl<>(products, pageable, 0);
        when(productService.getList(null, 5, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("stock", "-1")
                .param("page", "0")
                .param("size", "10");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

        verify(productService, never()).getList(null, 5, pageable);
    }

    @Test
    void getProductListWithSortFieldPrice() throws Exception{
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.ASC, "price"));
        List<Product> products = createTestProducts(20);
        Page<Product> mockPage = new PageImpl<>(products, pageable, 20);
        when(productService.getList(null, null, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "price,asc");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content.length()", equalTo(20)))
                .andExpect(jsonPath("$.totalElements", equalTo(20)))
                .andExpect(jsonPath("$.last", equalTo(false)));
        verify(productService).getList(null, null, pageable);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProductListWithSortFieldPrice_WhenSortFieldNotExists_ShouldThrowBadRequest() throws Exception{
        Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.ASC, "price"));
        List<Product> products = new ArrayList<>();
        Page<Product> mockPage = new PageImpl<>(products, pageable, 0);
        when(productService.getList(null, null, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "not_exists,asc");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
        verify(productService, never()).getList(null, null, pageable);
    }

    @Test
    void getProductListWithSortFieldPrice_WhenSortDirectionNotExists_ShouldThrowBadRequest() throws Exception{
        Pageable pageable = PageRequest.of(0,10, Sort.by("price", "asc"));
        List<Product> products = new ArrayList<>();
        Page<Product> mockPage = new PageImpl<>(products, pageable, 0);
        when(productService.getList(null, null, pageable)).thenReturn(mockPage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "price,not_exists");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductById() throws Exception{
        Long productId = 1L;
        when(productService.getById(productId)).thenReturn(Optional.of(savedProduct));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products/1");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.productName", equalTo("test product")))
                .andExpect(jsonPath("$.productId", equalTo(1)));
        verify(productService).getById(productId);
    }

    @Test
    void createProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testedProduct));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1L));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void updateProduct() throws Exception {
        when(productService.updateProduct(any(Long.class), any(Product.class))).thenReturn(savedProduct);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savedProduct));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("productId").value(1L))
                .andExpect(jsonPath("productName").value(savedProduct.getProductName()));
        verify(productService).updateProduct(any(Long.class), any(Product.class));

    }

    @Test
    void deleteProduct() throws Exception {
        Long productId = 1L;
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/v1/products/1");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId);
    }
}