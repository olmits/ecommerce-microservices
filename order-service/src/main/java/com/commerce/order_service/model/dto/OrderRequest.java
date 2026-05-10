package com.commerce.order_service.model.dto;


import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {
    public record OrderItemRequest(Long productId, Integer quantity) {}
}
