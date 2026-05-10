package com.commerce.inventory_service.service;

import com.commerce.inventory_service.config.RabbitMQConfig;
import com.commerce.inventory_service.models.dto.InventoryReservedEvent;
import com.commerce.inventory_service.models.dto.OrderCreatedEvent;
import com.commerce.inventory_service.models.entity.Inventory;
import com.commerce.inventory_service.models.entity.Product;
import com.commerce.inventory_service.repository.InventoryRepository;
import com.commerce.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.printf("Receive Order: %s\n", event.orderId());

        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        // Loop through the items in the order and deduct stock
        for (OrderCreatedEvent.OrderItemDto item : event.items()) {
            // 1. Check if the product actually exists in the catalog
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException(String.format("Product not found: %s", item.productId())));

            // 2. Check the inventory
            Inventory inventory = inventoryRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException(String.format("Inventory is missing for product: %s", item.productId())));

            // 3. Verify we have enough stock
            if (inventory.getStockQuantity() < item.quantity()) {
                throw new RuntimeException(String.format("Not enough stock for product: %s", item.productId()));
            }

            // 4. deduct the stock and save
            inventory.setStockQuantity(inventory.getStockQuantity() - item.quantity());
            inventoryRepository.save(inventory);

            // 5. Calculate the price for this item and add to the running total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.quantity()));
            totalOrderAmount = totalOrderAmount.add(itemTotal);

            System.out.printf("Deducted %s from Product %s\n", item.quantity(), item.productId());
        }

        // TODO: add constant?
        InventoryReservedEvent nextEvent = new InventoryReservedEvent(
                event.orderId(),
                totalOrderAmount,
                "INVENTORY_RESERVED"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "inventory.reserved",
                nextEvent
        );

        System.out.println("Inventory reserved! Message sent to Payment Service");
    }
}
