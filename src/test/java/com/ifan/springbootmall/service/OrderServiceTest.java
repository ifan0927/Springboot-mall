package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.order.NullOrderException;
import com.ifan.springbootmall.exception.order.OrderNotFoundException;
import com.ifan.springbootmall.exception.order.NullOrderItemException;
import com.ifan.springbootmall.exception.product.NotEnoughStockException;
import com.ifan.springbootmall.exception.product.ProductNotFoundException;
import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import com.ifan.springbootmall.repository.OrderRepository;
import com.ifan.springbootmall.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Order savedOrder;
    private List<OrderItem> testOrderItems;
    private List<OrderItem> savedOrderItems;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setUserId(1L);
        testOrder.setTotalAmount(1500);
        testOrder.setCreatedDate(LocalDateTime.now());
        testOrder.setLastModifiedDate(LocalDateTime.now());

        savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setUserId(1L);
        savedOrder.setTotalAmount(1500);
        savedOrder.setCreatedDate(LocalDateTime.now());
        savedOrder.setLastModifiedDate(LocalDateTime.now());

        testOrderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setOrderId(1L);
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setAmount(300);

        OrderItem item2 = new OrderItem();
        item2.setOrderId(1L);
        item2.setProductId(2L);
        item2.setQuantity(1);
        item2.setAmount(200);

        testOrderItems.add(item1);
        testOrderItems.add(item2);

        savedOrderItems = new ArrayList<>();
        OrderItem savedItem1 = new OrderItem();
        savedItem1.setId(1L);
        savedItem1.setOrderId(1L);
        savedItem1.setProductId(1L);
        savedItem1.setQuantity(2);
        savedItem1.setAmount(300);

        OrderItem savedItem2 = new OrderItem();
        savedItem2.setId(2L);
        savedItem2.setOrderId(1L);
        savedItem2.setProductId(2L);
        savedItem2.setQuantity(1);
        savedItem2.setAmount(200);

        savedOrderItems.add(savedItem1);
        savedOrderItems.add(savedItem2);
    }

    private List<Order> createTestOrders(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Order order = new Order();
            order.setOrderId(ids.get(i).longValue());
            order.setUserId(1L);
            order.setTotalAmount(i * 100);
            order.setCreatedDate(LocalDateTime.now());
            order.setLastModifiedDate(LocalDateTime.now());
            orders.add(order);
        }
        return orders;
    }

    private List<OrderItem> createTestOrderItems(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);

        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(ids.get(i).longValue());
            orderItem.setOrderId(1L);
            orderItem.setProductId(ids.get(i).longValue());
            orderItem.setQuantity(i + 1);
            orderItem.setAmount((i + 1) * 100);
            orderItems.add(orderItem);
        }
        return orderItems;
    }


    @Test
    void findAllOrders_ShouldReturnAllOrders() {
        List<Order> orders = createTestOrders(5);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.findAllOrders();

        assertEquals(5, result.size());
        assertEquals(orders, result);
        verify(orderRepository).findAll();
    }

    @Test
    void findAllOrders_WhenNoOrders_ShouldReturnEmptyList() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        List<Order> result = orderService.findAllOrders();

        assertTrue(result.isEmpty());
        verify(orderRepository).findAll();
    }


    @Test
    void findOrderById_WhenOrderExists_ShouldReturnOrder() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));

        Optional<Order> result = orderService.findOrderById(orderId);

        assertTrue(result.isPresent());
        assertEquals(savedOrder, result.get());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void findOrderById_WhenOrderNotExists_ShouldReturnEmptyOptional() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.findOrderById(orderId);

        assertTrue(result.isEmpty());
        verify(orderRepository).findById(orderId);
    }


    @Test
    void findOrdersByUserId_WithPagination_ShouldReturnPagedResult() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = createTestOrders(20);
        Page<Order> mockPage = new PageImpl<>(orders, pageable, 20);

        when(orderRepository.findByUserId(pageable, userId)).thenReturn(mockPage);

        Page<Order> result = orderService.findOrdersByUserId(userId, pageable);

        assertEquals(mockPage, result);
        verify(orderRepository).findByUserId(pageable, userId);
    }


    @Test
    void createOrder_WhenOrderAndOrderItemsExist_ShouldReturnSavedOrder() {
        when(productService.hasEnoughStock(1L, 2)).thenReturn(true);
        when(productService.hasEnoughStock(2L, 1)).thenReturn(true);
        when(orderRepository.save(testOrder)).thenReturn(savedOrder);
        when(orderItemRepository.saveAll(testOrderItems)).thenReturn(savedOrderItems);

        Order result = orderService.createOrder(testOrder, testOrderItems);

        assertEquals(savedOrder, result);
        verify(productService).hasEnoughStock(1L, 2);
        verify(productService).hasEnoughStock(2L, 1);
        verify(productService).decreaseStock(1L, 2);
        verify(productService).decreaseStock(2L, 1);
        verify(orderRepository).save(testOrder);
        verify(orderItemRepository).saveAll(testOrderItems);
    }

    @Test
    void createOrder_WhenStockIsNotSufficient_ShouldThrowException() {
        when(productService.hasEnoughStock(1L, 2)).thenReturn(false);

        RuntimeException exception = assertThrows(NotEnoughStockException.class,
            () -> orderService.createOrder(testOrder, testOrderItems));

        assertEquals("Not enough stock", exception.getMessage());
        verify(productService).hasEnoughStock(1L, 2);
        verify(productService, never()).decreaseStock(any(), any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void createOrder_WhenProductNotExists_ShouldThrowException() {
        when(productService.hasEnoughStock(1L, 2)).thenThrow(new ProductNotFoundException(1L));

        RuntimeException exception = assertThrows(ProductNotFoundException.class,
            () -> orderService.createOrder(testOrder, testOrderItems));

        assertEquals("Product not found: 1", exception.getMessage());
        verify(productService).hasEnoughStock(1L, 2);
        verify(productService, never()).decreaseStock(any(), any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void createOrder_WhenOrderIsNull_ShouldThrowException() {
        RuntimeException exception = assertThrows(NullOrderException.class,
            () -> orderService.createOrder(null, testOrderItems));

        assertEquals("Order is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void createOrder_WhenOrderItemsIsNull_ShouldThrowException() {
        RuntimeException exception = assertThrows(NullOrderItemException.class,
            () -> orderService.createOrder(testOrder, null));

        assertEquals("OrderItem is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void createOrder_WhenOrderItemsIsEmpty_ShouldThrowException() {
        List<OrderItem> emptyOrderItems = new ArrayList<>();

        RuntimeException exception = assertThrows(NullOrderItemException.class,
            () -> orderService.createOrder(testOrder, emptyOrderItems));

        assertEquals("OrderItem is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }


    @Test
    void updateOrder_WhenOrderExistsAndOrderItemsExist_ShouldReturnUpdatedOrder() {
        Long orderId = 1L;
        Order updateData = new Order();
        updateData.setTotalAmount(2000);

        List<OrderItem> updateOrderItems = new ArrayList<>();
        OrderItem updateItem = new OrderItem();
        updateItem.setProductId(3L);
        updateItem.setQuantity(5);
        updateItem.setAmount(500);
        updateOrderItems.add(updateItem);

        Order expectedUpdatedOrder = new Order();
        expectedUpdatedOrder.setOrderId(orderId);
        expectedUpdatedOrder.setTotalAmount(2000);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        when(productService.hasEnoughStock(3L, 5)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(expectedUpdatedOrder);
        when(orderItemRepository.saveAll(updateOrderItems)).thenReturn(updateOrderItems);

        Order result = orderService.updateOrder(orderId, updateData, updateOrderItems);

        assertEquals(expectedUpdatedOrder, result);
        verify(orderRepository).findById(orderId);
        verify(productService).hasEnoughStock(3L, 5);
        verify(productService).decreaseStock(3L, 5);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(updateOrderItems);
    }

    @Test
    void updateOrder_WhenStockIsNotSufficient_ShouldThrowException() {
        // GIVEN
        Long orderId = 1L;
        List<OrderItem> updateOrderItems = new ArrayList<>();
        OrderItem updateItem = new OrderItem();
        updateItem.setProductId(3L);
        updateItem.setQuantity(100); // 假設庫存不足
        updateOrderItems.add(updateItem);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        when(productService.hasEnoughStock(3L, 100)).thenReturn(false);

        // WHEN & THEN
        RuntimeException exception = assertThrows(NotEnoughStockException.class,
            () -> orderService.updateOrder(orderId, testOrder, updateOrderItems));

        assertEquals("Not enough stock", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(productService).hasEnoughStock(3L, 100);
        verify(productService, never()).decreaseStock(any(), any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void updateOrder_WhenProductNotExists_ShouldThrowException() {
        Long orderId = 1L;
        List<OrderItem> updateOrderItems = new ArrayList<>();
        OrderItem updateItem = new OrderItem();
        updateItem.setProductId(999L); // 不存在的商品
        updateItem.setQuantity(1);
        updateOrderItems.add(updateItem);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(savedOrder));
        when(productService.hasEnoughStock(999L, 1)).thenThrow(new ProductNotFoundException(999L));

        RuntimeException exception = assertThrows(ProductNotFoundException.class,
            () -> orderService.updateOrder(orderId, testOrder, updateOrderItems));

        assertEquals("Product not found: 999", exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(productService).hasEnoughStock(999L, 1);
        verify(productService, never()).decreaseStock(any(), any());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void updateOrder_WhenOrderNotExists_ShouldThrowException() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(OrderNotFoundException.class,
            () -> orderService.updateOrder(orderId, testOrder, testOrderItems));

        assertEquals("Order not found: " + orderId, exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void updateOrder_WhenOrderIsNull_ShouldThrowException() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
        RuntimeException exception = assertThrows(NullOrderException.class,
            () -> orderService.updateOrder(1L, null, testOrderItems));

        assertEquals("Order is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void updateOrder_WhenOrderItemsIsNull_ShouldThrowException() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
        RuntimeException exception = assertThrows(NullOrderItemException.class,
            () -> orderService.updateOrder(1L, testOrder, null));

        assertEquals("OrderItem is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }

    @Test
    void updateOrder_WhenOrderItemsIsEmpty_ShouldThrowException() {
        List<OrderItem> emptyOrderItems = new ArrayList<>();
        when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
        RuntimeException exception = assertThrows(NullOrderItemException.class,
            () -> orderService.updateOrder(1L, testOrder, emptyOrderItems));

        assertEquals("OrderItem is null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).saveAll(any());
    }


    @Test
    void deleteOrderAndOrderItem_WhenOrderExists_ShouldDelete() {
        Long orderId = 1L;

        orderService.deleteOrderAndOrderItem(orderId);

        verify(orderItemRepository).deleteByOrderId(orderId);
        verify(orderRepository).deleteById(orderId);
    }


    @Test
    void findOrderItemsByOrderId_WithPagination_ShouldReturnPagedResult() {
        Long orderId = 1L;
        List<OrderItem> orderItems = createTestOrderItems(15);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(orderItems);

        List<OrderItem> result = orderService.findOrderItemsByOrderId(orderId);

        assertEquals(orderItems, result);
        verify(orderItemRepository).findByOrderId(orderId);
    }
}