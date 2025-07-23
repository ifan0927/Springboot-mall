package com.ifan.springbootmall.controller;

import com.ifan.springbootmall.model.Order;
import com.ifan.springbootmall.model.OrderItem;
import com.ifan.springbootmall.service.OrderService;
import com.ifan.springbootmall.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtService jwtService;

    /**
     * 取得指定訂單詳情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId,
                                              @RequestHeader String authorization) {
        // TODO: 實作取得訂單詳情邏輯
        // 1. 從 JWT token 取得使用者資訊
        // 2. 驗證訂單歸屬權限
        // 3. 呼叫 orderService.findOrderById(orderId)
        // 4. 回傳結果或適當的錯誤狀態
        return null;
    }

    /**
     * 取得使用者的訂單列表（分頁）
     */
    @GetMapping("/my")
    public ResponseEntity<Page<Order>> getMyOrders(@RequestHeader String authorization,
                                                   Pageable pageable) {
        // TODO: 實作取得使用者訂單列表邏輯
        // 1. 從 JWT token 取得使用者 ID
        // 2. 驗證 pageable 參數
        // 3. 呼叫 orderService.findOrdersByUserId(userId, pageable)
        // 4. 回傳分頁結果
        return null;
    }

    /**
     * 取得訂單的商品項目列表
     */
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId,
                                                         @RequestHeader String authorization) {
        // TODO: 實作取得訂單商品項目邏輯
        // 1. 從 JWT token 取得使用者資訊
        // 2. 驗證訂單歸屬權限
        // 3. 呼叫 orderService.findOrderItemsByOrderId(orderId)
        // 4. 回傳商品項目列表
        return null;
    }

    /**
     * 建立新訂單
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> requestBody,
                                             @RequestHeader String authorization) {
        // TODO: 實作建立訂單邏輯
        // 1. 從 JWT token 取得使用者 ID
        // 2. 驗證 requestBody 格式（order + orderItems）
        // 3. 建立 Order 物件並設定 userId
        // 4. 解析 orderItems 清單
        // 5. 呼叫 orderService.createOrder(order, orderItems)
        // 6. 處理相關異常（庫存不足、商品不存在等）
        // 7. 回傳 201 Created 或適當的錯誤狀態
        return null;
    }

    /**
     * 更新訂單
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId,
                                             @RequestBody Map<String, Object> requestBody,
                                             @RequestHeader String authorization) {
        // TODO: 實作更新訂單邏輯
        // 1. 從 JWT token 取得使用者 ID
        // 2. 驗證訂單歸屬權限
        // 3. 驗證 requestBody 格式
        // 4. 解析更新的 order 和 orderItems
        // 5. 呼叫 orderService.updateOrder(orderId, order, orderItems)
        // 6. 處理相關異常
        // 7. 回傳更新結果或適當的錯誤狀態
        return null;
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
        return null;
    }
}