package com.commerce.order_service.service;

import com.commerce.order_service.config.RabbitMqConfig;
import com.commerce.order_service.model.dto.OrderCreatedEvent;
import com.commerce.order_service.model.dto.OrderRequest;
import com.commerce.order_service.model.entity.Order;
import com.commerce.order_service.model.entity.OrderItem;
import com.commerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setStatus("PENDING");

        List<OrderItem> orderItems = request.items().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setProductId(itemReq.productId());
            item.setQuantity(itemReq.quantity());
            item.setOrder(order); // Link back to parent
            return item;
        }).toList();

        order.setItems(orderItems);

        //        Save to PostgreSQL
        Order savedOrder = orderRepository.save(order);

        //        Map to RabbitMQ Event
        List<OrderCreatedEvent.OrderItemDto> eventItems = savedOrder.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemDto(item.getProductId(), item.getQuantity()))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(), eventItems);

        //        Publish to RMQ
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, event);

        return savedOrder;
    }
}
