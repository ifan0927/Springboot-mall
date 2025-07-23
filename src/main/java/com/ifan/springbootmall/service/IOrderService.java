package com.ifan.springbootmall.service;

import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IOrderService {

    List<Order> findAllOrders();
    Optional<Order> findOrderById(Long orderId);
    Page<Order> findOrdersByUserId(Long userId, Pageable pageable);
    Order createOrder(Order order, List<OrderItem> orderItem);
    Order updateOrder(Long orderId, Order order, List<OrderItem> orderItem);
    void deleteOrderAndOrderItem(Long orderId);
    List<OrderItem> findOrderItemsByOrderId(Long orderId);


}
