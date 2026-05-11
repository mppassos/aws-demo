package com.matheus.awsdemo.service;

import com.matheus.awsdemo.dto.OrderRequest;
import com.matheus.awsdemo.model.Order;
import com.matheus.awsdemo.model.OrderEvent;
import com.matheus.awsdemo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public Order createOrder(OrderRequest request) {
        // 1. Criar pedido
        Order order = Order.create(request.getCustomerId(), request.getTotalAmount());

        // 2. Salvar no DynamoDB
        Order savedOrder = orderRepository.save(order);
        log.info("Pedido salvo: {}", savedOrder.getOrderId());

        // 3. Publicar evento (Outbox Pattern simplificado)
        OrderEvent event = OrderEvent.builder()
                .orderId(savedOrder.getOrderId())
                .eventType("ORDER_CREATED")
                .customerId(savedOrder.getCustomerId())
                .totalAmount(savedOrder.getTotalAmount())
                .timestamp(Instant.now())
                .build();

        eventPublisher.publishOrderCreated(event);

        return savedOrder;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + orderId));
    }
}