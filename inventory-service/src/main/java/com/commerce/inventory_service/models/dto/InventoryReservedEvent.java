package com.commerce.inventory_service.models.dto;

public record InventoryReservedEvent(Long orderId, String status) {
}
