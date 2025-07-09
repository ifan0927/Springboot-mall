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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ifan.springbootmall.service.ProductService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        testedProduct.setCategory("BOOKS");
        testedProduct.setImageUrl("test image url");
        testedProduct.setPrice(100);
        testedProduct.setStock(10);
        testedProduct.setDescription("test description");

        savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductName("test product");
        savedProduct.setCategory("BOOKS");
        savedProduct.setImageUrl("test image url");
        savedProduct.setPrice(100);
        savedProduct.setStock(10);
        savedProduct.setDescription("test description");
    }

    @Test
    void getProductList() throws Exception {
        when(productService.getList()).thenReturn(List.of(testedProduct));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("test product"));
        verify(productService).getList();
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
    void getProductListByCategory() throws Exception{
        ProductCategory category = ProductCategory.BOOKS;
        when(productService.getListByCategory(category)).thenReturn(List.of(savedProduct));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products/category/test category");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].productName", equalTo("test product")))
                .andExpect(jsonPath("$[0].productId", equalTo(1)));

        verify(productService).getListByCategory(category);
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