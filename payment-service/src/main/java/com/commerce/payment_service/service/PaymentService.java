package com.commerce.payment_service.service;

import com.commerce.payment_service.config.RabbitMqConfig;
import com.commerce.payment_service.model.dto.InventoryReservedEvent;
import com.commerce.payment_service.model.dto.PaymentFailedEvent;
import com.commerce.payment_service.model.dto.PaymentProcessedEvent;
import com.commerce.payment_service.model.entity.Payment;
import com.commerce.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.PAYMENT_QUEUE)
    @Transactional
    public void processPayment(InventoryReservedEvent event) {
        System.out.printf("Processing payment for Order: %s | Amount: %s", event.orderId(), event.totalAmount());

        Payment payment = new Payment();
        payment.setOrderId(event.orderId());
        payment.setAmount(event.totalAmount());

        // Simulate Credit Card Processing Rules
        // Let's say any order over $1000 triggers a fraud alert and fails!
        if (event.totalAmount().compareTo(new BigDecimal("1000.00")) > 0) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            System.err.printf("Payment FAILED for Order: %s | Amount: %s", event.orderId(), event.totalAmount());

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE_NAME,
                    "payment.failed",
                    new PaymentFailedEvent(event.orderId(), "Credit limit exceeded")
            );
            return;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        System.out.printf("Payment SUCCESSFUL for Order: %s | Amount: %s", event.orderId(), event.totalAmount());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                "payment.successful",
                new PaymentProcessedEvent(event.orderId(), "PAYMENT_SUCCE")
        );
    }
}
