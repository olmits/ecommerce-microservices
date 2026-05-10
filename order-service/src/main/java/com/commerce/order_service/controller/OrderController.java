package com.commerce.order_service.controller;

import com.commerce.order_service.model.dto.OrderRequest;
import com.commerce.order_service.model.entity.Order;
import com.commerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request) {
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.ok(createdOrder);
    }
}
