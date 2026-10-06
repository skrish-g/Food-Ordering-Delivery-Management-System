package com.pwioi.app.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-group")
    public void consumeOrderCreated(String message) {

        try {
            JsonNode event = objectMapper.readTree(message);

            Long orderId = event.get("orderId").asLong();

            System.out.println(
                    "Your order #" + orderId +
                    " has been successfully placed.");

        } catch (Exception e) {
            System.out.println(
                    "Notification error: " + e.getMessage());
        }
    }
}