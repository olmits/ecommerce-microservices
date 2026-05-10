package com.commerce.inventory_service.models.dto;

import java.util.List;

public record OrderCreatedEvent(Long orderId, List<OrderItemDto> items) {
    public record OrderItemDto(Long productId, Integer quantity) {}
}
