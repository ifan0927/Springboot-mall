package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.exception.order.OrderNotFoundException;
import com.ifan.springbootmall.exception.order.NullOrderException;
import com.ifan.springbootmall.exception.order.NullOrderItemException;
import com.ifan.springbootmall.exception.product.ProductNotFoundException;
import com.ifan.springbootmall.exception.product.NotEnoughStockException;
import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.security.JwtService;
import com.ifan.springbootmall.service.OrderService;
import com.ifan.springbootmall.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifan.springbootmall.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private Order testOrder;
    private List<Order> testOrders;
    private List<OrderItem> testOrderItems;
    private String fullToken;
    private String userEmail;
    private Long userId;
    private User testedUser;
    private User savedUser;

    @BeforeEach
    void setUp() {
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("test@gmail.com");
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());
        when(customUserDetailsService.loadUserByUsername("test@gmail.com"))
            .thenReturn(mockUserDetails);

        // 修正 token 設定
        String mockToken = "mock-jwt-token";
        fullToken = "Bearer " + mockToken;

        // Mock JWT service 對任何字串都返回正確的 email
        when(jwtService.getEmailFromToken(anyString())).thenReturn("test@gmail.com");

        testedUser = new User();
        testedUser.setEmail("test@gmail.com");
        testedUser.setPassword("password123");

        savedUser = new User();
        savedUser.setEmail("test@gmail.com");
        savedUser.setPassword("password123");
        savedUser.setUserId(1L);

        userEmail = "test@gmail.com";
        userId = 1L;

        // 測試用的 Order
        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUserId(userId);
        testOrder.setTotalAmount(1500);
        testOrder.setCreatedDate(LocalDateTime.now());
        testOrder.setLastModifiedDate(LocalDateTime.now());

        // 測試用的 Order 列表
        testOrders = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Order order = new Order();
            order.setOrderId((long) i);
            order.setUserId(userId);
            order.setTotalAmount(i * 1000);
            order.setCreatedDate(LocalDateTime.now());
            order.setLastModifiedDate(LocalDateTime.now());
            testOrders.add(order);
        }

        // 測試用的 OrderItem 列表
        testOrderItems = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            OrderItem item = new OrderItem();
            item.setId((long) i);
            item.setOrderId(1L);
            item.setProductId((long) i);
            item.setQuantity(i);
            item.setAmount(i * 500);
            testOrderItems.add(item);
        }
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getOrderById_WhenOrderExistsAndUserIsOwner_ShouldReturnOrder() throws Exception {
        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(1L)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/v1/orders/1")
                        .header("authorization", fullToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalAmount").value(1500));

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(1L);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getOrderById_WhenOrderNotExists_ShouldReturnNotFound() throws Exception {
        // GIVEN
        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/orders/999")
                        .header("authorization", fullToken))
                .andExpect(status().isNotFound());

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(999L);
    }


    @Test
    @WithMockUser(username = "test@gmail.com")
    void getMyOrders_WhenValidToken_ShouldReturnUserOrders() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(testOrders, pageable, testOrders.size());

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrdersByUserId(eq(userId), any(Pageable.class))).thenReturn(orderPage);

        mockMvc.perform(get("/api/v1/orders/my?page=0&size=10")
                        .header("authorization", fullToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.totalElements").value(3));

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrdersByUserId(eq(userId), any(Pageable.class));
    }


    @Test
    @WithMockUser(username = "test@gmail.com")
    void getOrderItems_WhenOrderExistsAndUserIsOwner_ShouldReturnOrderItems() throws Exception {
        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderService.findOrderItemsByOrderId(1L)).thenReturn(testOrderItems);

        mockMvc.perform(get("/api/v1/orders/1/items")
                        .header("authorization", fullToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(1));

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(1L);
        verify(orderService).findOrderItemsByOrderId(1L);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getOrderItems_WhenOrderNotExists_ShouldReturnNotFound() throws Exception {
        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(orderService.findOrderById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/999/items")
                        .header("authorization", fullToken))
                .andExpect(status().isNotFound());

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(999L);
        verify(orderService, never()).findOrderItemsByOrderId(any());
    }


    @Test
    @WithMockUser(username = "test@gmail.com")
    void createOrder_WhenValidRequest_ShouldCreateOrderSuccessfully() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 1500);

        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("quantity", 2);
        item1.put("amount", 300);
        orderItems.add(item1);

        requestBody.put("order", orderData);
        requestBody.put("orderItems", orderItems);

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.createOrder(any(Order.class), anyList())).thenReturn(testOrder);

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(1500));

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).createOrder(any(Order.class), anyList());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createOrder_WhenNullOrder_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order", null);
        requestBody.put("orderItems", Collections.emptyList());

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.createOrder(any(), any())).thenThrow(new NullOrderException());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(jwtService).getEmailFromToken(fullToken);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createOrder_WhenNullOrderItems_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 1500);
        requestBody.put("order", orderData);
        requestBody.put("orderItems", null);

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.createOrder(any(), any())).thenThrow(new NullOrderItemException());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(jwtService).getEmailFromToken(fullToken);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createOrder_WhenProductNotFound_ShouldReturnBadRequest() throws Exception {
        System.out.println("fullToken: " + fullToken);
        System.out.println("userEmail: " + userEmail);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 1500);

        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 999L);
        item1.put("quantity", 2);
        orderItems.add(item1);

        requestBody.put("order", orderData);
        requestBody.put("orderItems", orderItems);

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.createOrder(any(), any())).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(jwtService).getEmailFromToken(fullToken);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void createOrder_WhenNotEnoughStock_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 1500);

        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("quantity", 100);
        orderItems.add(item1);

        requestBody.put("order", orderData);
        requestBody.put("orderItems", orderItems);

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.createOrder(any(), any())).thenThrow(new NotEnoughStockException());

        mockMvc.perform(post("/api/v1/orders")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(jwtService).getEmailFromToken(fullToken);
    }


    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateOrder_WhenValidRequest_ShouldUpdateOrderSuccessfully() throws Exception {
        Long orderId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 2000);

        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 2L);
        item1.put("quantity", 3);
        item1.put("amount", 600);
        orderItems.add(item1);

        requestBody.put("order", orderData);
        requestBody.put("orderItems", orderItems);

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setUserId(userId);
        updatedOrder.setTotalAmount(2000);

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderService.updateOrder(eq(orderId), any(Order.class), anyList())).thenReturn(updatedOrder);

        mockMvc.perform(put("/api/v1/orders/1")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(2000));

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(orderId);
        verify(orderService).updateOrder(eq(orderId), any(Order.class), anyList());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateOrder_WhenOrderNotExists_ShouldReturnNotFound() throws Exception {
        Long orderId = 999L;
        Map<String, Object> requestBody = new HashMap<>();

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/orders/999")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(orderId);
        verify(orderService, never()).updateOrder(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateOrder_WhenOrderNotFoundInService_ShouldReturnNotFound() throws Exception {
        Long orderId = 1L;
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("totalAmount", 2000);
        requestBody.put("order", orderData);
        requestBody.put("orderItems", Collections.emptyList());

        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderService.updateOrder(eq(orderId), any(), any())).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(put("/api/v1/orders/1")
                        .with(csrf())
                        .header("authorization", fullToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(orderId);
    }


    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteOrder_WhenOrderExistsAndUserIsOwner_ShouldDeleteSuccessfully() throws Exception {
        Long orderId = 1L;
        when(jwtService.getEmailFromToken(fullToken)).thenReturn(userEmail);
        when(userService.getByEmail(userEmail)).thenReturn(Optional.of(savedUser));
        when(orderService.findOrderById(orderId)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(delete("/api/v1/orders/1")
                        .with(csrf())
                        .header("authorization", fullToken))
                .andExpect(status().isNoContent());

        verify(jwtService).getEmailFromToken(fullToken);
        verify(orderService).findOrderById(orderId);
        verify(orderService).deleteOrderAndOrderItem(orderId);
    }


}