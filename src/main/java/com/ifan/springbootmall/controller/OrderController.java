package com.ifan.springbootmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    @GetMapping
    public String createOrder() {
        return "success";
    }
}
