package com.commerce.inventory_service.service;

import com.commerce.inventory_service.config.RabbitMQConfig;
import com.commerce.inventory_service.models.dto.InventoryReservedEvent;
import com.commerce.inventory_service.models.dto.OrderCreatedEvent;
import com.commerce.inventory_service.models.entity.Inventory;
import com.commerce.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.printf("Receive Order: %s\n", event.orderId());

        // Loop through the items in the order and deduct stock
        for (OrderCreatedEvent.OrderItemDto item : event.items()) {
            Inventory inventory = inventoryRepository.findByProductId(item.productId())
                    .orElseThrow(() -> new RuntimeException(String.format("Product not found in inventory: %s", item.productId())));

            if (inventory.getStockQuantity() < item.quantity()) {
                throw new RuntimeException(String.format("Not enough stock for product: %s", item.productId()));
            }

            inventory.setStockQuantity(inventory.getStockQuantity() - item.quantity());
            inventoryRepository.save(inventory);
            System.out.printf("Deducted %s from Product %s\n", item.quantity(), item.productId());
        }

        // TODO: add constant?
        InventoryReservedEvent nextEvent = new InventoryReservedEvent(event.orderId(), "INVENTORY_RESERVED");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "inventory.reserved",
                nextEvent
        );

        System.out.println("Inventory reserved! Message sent to Payment Service");
    }
}
