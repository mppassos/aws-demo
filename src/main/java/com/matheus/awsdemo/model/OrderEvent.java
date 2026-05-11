package com.matheus.awsdemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventId;
    private String orderId;
    private String eventType; // CREATED, UPDATED, CANCELLED
    private String customerId;
    private Double totalAmount;
    private Instant timestamp;
}