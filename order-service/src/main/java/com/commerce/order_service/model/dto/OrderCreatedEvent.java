package com.commerce.order_service.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(Long orderId, BigDecimal totalAmount, List<OrderItemDto> items) {
    public record OrderItemDto(Long productId, Integer quantity) {}
}
