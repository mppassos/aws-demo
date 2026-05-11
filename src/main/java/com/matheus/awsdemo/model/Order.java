package com.matheus.awsdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order {
    private String orderId;
    private String customerId;
    private Double totalAmount;
    private String status;
    private Instant createdAt;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }

    public static Order create(String customerId, Double totalAmount) {
        return Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(customerId)
                .totalAmount(totalAmount)
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
    }
}