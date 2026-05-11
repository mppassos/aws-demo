package com.matheus.awsdemo.repository;

import com.matheus.awsdemo.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class OrderRepository {

    private final DynamoDbTable<Order> orderTable;

    public OrderRepository(
            DynamoDbEnhancedClient enhancedClient,
            @Value("${aws.dynamodb.table-name}") String tableName) {
        this.orderTable = enhancedClient.table(tableName,
                TableSchema.fromBean(Order.class));
    }

    public Order save(Order order) {
        orderTable.putItem(order);
        return order;
    }

    public Optional<Order> findById(String orderId) {
        Order order = orderTable.getItem(
                Key.builder().partitionValue(orderId).build());
        return Optional.ofNullable(order);
    }
}