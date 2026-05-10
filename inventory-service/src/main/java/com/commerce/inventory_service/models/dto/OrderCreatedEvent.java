package com.commerce.inventory_service.models.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(Long orderId, BigDecimal totalAmount, List<OrderItemDto> items) {
    public record OrderItemDto(Long productId, Integer quantity) {}
}
