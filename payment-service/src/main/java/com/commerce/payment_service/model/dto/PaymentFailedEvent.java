package com.commerce.payment_service.model.dto;

public record PaymentFailedEvent(Long orderId, String reason) {
}
