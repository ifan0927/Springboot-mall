package controller;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import service.ProductService;

import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private ProductService productService;
    private Product testedProduct;
    private Product savedProduct;

    @BeforeEach
    void setUp(){
        testedProduct = new Product();
        testedProduct.setProductName("test product");
        testedProduct.setCategory("test category");
        testedProduct.setImageUrl("test image url");
        testedProduct.setPrice(100);
        testedProduct.setStock(10);
        testedProduct.setDescription("test description");

        savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductName("test product");
        savedProduct.setCategory("test category");
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
        when(productService.getById(productId)).thenReturn(Optional.of(testedProduct));
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
        String category = "test category";
        when(productService.getListByCategory(category)).thenReturn(List.of(testedProduct));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/products/category/test category");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].productName", equalTo("test product")))
                .andExpect(jsonPath("$[0].productId", equalTo(1)));
    }

    @Test
    void createProduct() throws Exception{
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }
}