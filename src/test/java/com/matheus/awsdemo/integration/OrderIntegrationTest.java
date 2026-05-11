package com.matheus.awsdemo.integration;

import com.matheus.awsdemo.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
            .withServices(DYNAMODB, SNS, SQS);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateOrderAndPersistInDynamoDB() {
        // Arrange
        OrderRequest request = new OrderRequest("customer-123", 150.00);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders", request, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("customer-123");
        assertThat(response.getBody()).contains("PENDING");
    }

    @Test
    void shouldValidateOrderRequest() {
        // Arrange
        OrderRequest invalidRequest = new OrderRequest("", -10.0);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders", invalidRequest, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}