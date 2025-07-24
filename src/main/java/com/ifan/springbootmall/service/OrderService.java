package com.ifan.springbootmall.service;

import com.ifan.springbootmall.exception.order.NullOrderException;
import com.ifan.springbootmall.exception.order.NullOrderItemException;
import com.ifan.springbootmall.exception.order.OrderNotFoundException;
import com.ifan.springbootmall.exception.product.NotEnoughStockException;
import com.ifan.springbootmall.exception.product.ProductNotFoundException;
import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import com.ifan.springbootmall.repository.OrderItemRepository;
import com.ifan.springbootmall.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService{

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    private ProductService productService;

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Page<Order> findOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(pageable, userId);
    }

    @Override
    public Order createOrder(Order order, List<OrderItem> orderItems) {
        int totalAmount;
        totalAmount = 0;
        if (order == null) {
            throw new NullOrderException();
        }
        if (orderItems == null || orderItems.isEmpty()) {
            throw new NullOrderItemException();
        }

        return getOrder(order, orderItems, totalAmount);

    }

    @Override
    public Order updateOrder(Long orderId, Order order, List<OrderItem> orderItem) {
        Optional<Order> existOrder = orderRepository.findById(orderId);
        if (existOrder.isEmpty()) {throw new OrderNotFoundException(orderId);}
        if (order == null) {throw new NullOrderException();}
        if (orderItem == null || orderItem.isEmpty()) {throw new NullOrderItemException();}
        List<OrderItem> existOrderItems = orderItemRepository.findByOrderId(orderId);
        if (!existOrderItems.isEmpty()){
            try{
                for  (OrderItem item : existOrderItems) {
                    productService.restoreStock(item.getProductId(), item.getQuantity());
                }
            }
            catch (ProductNotFoundException e){throw e;}
        }
        orderItemRepository.deleteByOrderId(orderId);

        int totalAmount = 0;
        return getOrder(order, orderItem, totalAmount);
    }

    private Order getOrder(Order order, List<OrderItem> orderItem, int totalAmount) {
        for (OrderItem item : orderItem) {
            try{
                if(!productService.hasEnoughStock(item.getProductId(), item.getQuantity())){throw new NotEnoughStockException();}
                productService.decreaseStock(item.getProductId(), item.getQuantity());
                totalAmount = totalAmount + item.getQuantity() * productService.getProductPrice(item.getProductId());
            } catch (ProductNotFoundException | NotEnoughStockException e){throw e;}
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        for (OrderItem item : orderItem) {
            item.setOrderId(savedOrder.getOrderId());
        }
        orderItemRepository.saveAll(orderItem);

        return savedOrder;
    }

    @Override
    public void deleteOrderAndOrderItem(Long orderId) {
        orderItemRepository.deleteByOrderId(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

}
