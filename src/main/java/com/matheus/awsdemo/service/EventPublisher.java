package com.matheus.awsdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheus.awsdemo.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.UUID;

@Slf4j
@Service
public class EventPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String topicArn;

    public EventPublisher(
            SnsClient snsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sns.topic-arn}") String topicArn) {
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.topicArn = topicArn;
    }

    public void publishOrderCreated(OrderEvent event) {
        try {
            event.setEventId(UUID.randomUUID().toString());
            String message = objectMapper.writeValueAsString(event);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            snsClient.publish(request);
            log.info("Evento publicado: {}", event.getOrderId());

        } catch (JsonProcessingException e) {
            log.error("Erro ao serializar evento: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao publicar no SNS: {}", e.getMessage());
        }
    }
}