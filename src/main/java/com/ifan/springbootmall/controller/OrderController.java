package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.exception.order.OrderNotFoundException;
import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import com.ifan.springbootmall.model.User;
import com.ifan.springbootmall.service.OrderService;
import com.ifan.springbootmall.security.JwtService;
import com.ifan.springbootmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId,
                                              @RequestHeader String authorization) {
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);
        Optional<Order> order = orderService.findOrderById(orderId);
        if (order.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if (user.isEmpty() || !user.get().getUserId().equals(order.get().getUserId())){
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(order.get());
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Order>> getMyOrders(@RequestHeader String authorization,
                                                   Pageable pageable) {
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);

        Page<Order> orders = orderService.findOrdersByUserId(user.get().getUserId(), pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId,
                                                         @RequestHeader String authorization) {
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);
        Optional<Order> order = orderService.findOrderById(orderId);
        if (order.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if (user.isEmpty() || !user.get().getUserId().equals(order.get().getUserId())){
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(orderService.findOrderItemsByOrderId(orderId));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> requestBody,
                                             @RequestHeader String authorization) {
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);
        Long userId = user.get().getUserId();
        if (requestBody.get("order") == null || requestBody.get("orderItems") == null) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> orderData = safeGetMap(requestBody, "order");
        List<Map<String, Object>> orderItemsData = safeGetList(requestBody, "orderItems");

        Order order;
        order = new Order();
        order.setUserId(userId);

        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemData : orderItemsData) {
            OrderItem item;
            item = new OrderItem();
            Long productId = ((Number) itemData.get("productId")).longValue();
            Integer quantity = (Integer) itemData.get("quantity");
            Integer amount = null;
            if (itemData.containsKey("amount")) {
                amount = (Integer) itemData.get("amount");
            }

            // 設定 OrderItem 屬性
            item.setProductId(productId);
            item.setQuantity(quantity);
            if (amount != null) {
                item.setAmount(amount);
            }

            orderItems.add(item);
        }
        Order createdOrder;
        try{
            createdOrder = orderService.createOrder(order, orderItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId,
                                             @RequestBody Map<String, Object> requestBody,
                                             @RequestHeader String authorization) {
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);
        Long userId = user.get().getUserId();
        Optional<Order> existOrder = orderService.findOrderById(orderId);
        if (existOrder.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        if (!existOrder.get().getUserId().equals(userId)){
            return ResponseEntity.status(403).build();
        }
        if  (requestBody.get("order") == null || requestBody.get("orderItems") == null) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> orderData = safeGetMap(requestBody, "order");
        List<Map<String, Object>> orderItemsData = safeGetList(requestBody, "orderItems");

        Order order;
        order = new Order();
        order.setUserId(userId);

        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> itemData : orderItemsData) {
            OrderItem item;
            item = new OrderItem();
            Long productId = ((Number) itemData.get("productId")).longValue();
            Integer quantity = (Integer) itemData.get("quantity");
            Integer amount = null;
            if (itemData.containsKey("amount")) {
                amount = (Integer) itemData.get("amount");
            }

            // 設定 OrderItem 屬性
            item.setProductId(productId);
            item.setQuantity(quantity);
            if (amount != null) {
                item.setAmount(amount);
            }

            orderItems.add(item);
        }
        Order updatedOrder;
        updatedOrder = new Order();
        try{
            updatedOrder = orderService.updateOrder(orderId, updatedOrder,orderItems);
        }
        catch (OrderNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updatedOrder);

    }

    /**
     * 刪除訂單
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId,
                                            @RequestHeader String authorization) {
        // TODO: 實作刪除訂單邏輯
        // 1. 從 JWT token 取得使用者 ID
        // 2. 驗證訂單歸屬權限
        // 3. 呼叫 orderService.deleteOrderAndOrderItem(orderId)
        // 4. 回傳 204 No Content 或適當的錯誤狀態
        String email = jwtService.getEmailFromToken(authorization);
        Optional<User> user = userService.getByEmail(email);
        Optional<Order> existOrder = orderService.findOrderById(orderId);
        if (existOrder.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        if (!existOrder.get().getUserId().equals(user.get().getUserId())){
            return ResponseEntity.status(403).build();
        }
        orderService.deleteOrderAndOrderItem(orderId);
        return ResponseEntity.noContent().build();
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> safeGetMap(Map<String, Object> source, String key) {
        Object obj = source.get(key);
        if (obj == null) return null;
        if (!(obj instanceof Map)) {
            throw new IllegalArgumentException(key + " must be a Map");
        }
        return (Map<String, Object>) obj;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> safeGetList(Map<String, Object> source, String key) {
        Object obj = source.get(key);
        if (obj == null) return null;
        if (!(obj instanceof List)) {
            throw new IllegalArgumentException(key + " must be a List");
        }
        return (List<Map<String, Object>>) obj;
    }

}