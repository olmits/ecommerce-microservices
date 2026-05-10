package com.commerce.order_service.model.dto;

import java.util.List;

public record OrderCreatedEvent(Long orderId, List<OrderItemDto> items) {
    public record OrderItemDto(Long productId, Integer quantity) {}
}
