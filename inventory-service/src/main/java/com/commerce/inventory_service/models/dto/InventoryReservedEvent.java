package com.commerce.inventory_service.models.dto;

import java.math.BigDecimal;

public record InventoryReservedEvent(Long orderId, BigDecimal totalAmount, String status) {
}
