package com.commerce.payment_service.model.dto;

public record PaymentProcessedEvent(Long orderId, String status) {
}
