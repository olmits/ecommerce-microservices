package com.commerce.payment_service.model.dto;

import java.math.BigDecimal;

public record InventoryReservedEvent(Long orderId, BigDecimal totalAmount, String status) { }
