package com.pwioi.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pwioi.app.client.FoodClient;
import com.pwioi.app.client.PaymentClient;
import com.pwioi.app.dto.FoodResponse;
import com.pwioi.app.dto.OrderItemRequest;
import com.pwioi.app.dto.OrderRequest;
import com.pwioi.app.dto.PaymentRequest;
import com.pwioi.app.dto.PaymentResponse;
import com.pwioi.app.entity.Order;
import com.pwioi.app.entity.OrderItem;
import com.pwioi.app.kafka.KafkaProducerService;
import com.pwioi.app.repository.OrderItemRepository;
import com.pwioi.app.repository.OrderRepository;

@Service
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodClient foodClient;
    private final PaymentClient paymentClient;
    
    private final KafkaProducerService kafkaProducerService;

    public OrderManagementService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            FoodClient foodClient,
            PaymentClient paymentClient,
            KafkaProducerService kafkaProducerService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.foodClient = foodClient;
        this.paymentClient = paymentClient;
        this.kafkaProducerService = kafkaProducerService;
    }

    public Order createOrder(OrderRequest request) {

        double total = 0;

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setRestaurantId(request.getRestaurantId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(0.0);

        order = orderRepository.save(order);

        for (OrderItemRequest itemRequest : request.getItems()) {

            FoodResponse food =
                    foodClient.getFood(itemRequest.getFoodId());

            if (!food.isAvailability()) {
                throw new RuntimeException(
                        "Food is not available: "
                                + food.getFoodName());
            }

            double itemTotal =
                    food.getPrice() * itemRequest.getQuantity();

            total += itemTotal;

            OrderItem item = new OrderItem();
            item.setOrderId(order.getOrderId());
            item.setFoodId(food.getFoodId());
            item.setQuantity(itemRequest.getQuantity());
            item.setPrice(food.getPrice());

            orderItemRepository.save(item);
        }

        order.setTotalAmount(total);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getOrderId());
        paymentRequest.setAmount(total);

        PaymentResponse payment =
                paymentClient.processPayment(paymentRequest);

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            order.setStatus("PLACED");
        } else {
            order.setStatus("FAILED");
        }

        order = orderRepository.save(order);

        if ("PLACED".equals(order.getStatus())) {

            String message = "{\"orderId\":" + order.getOrderId()
                    + ",\"customerId\":" + order.getCustomerId()
                    + ",\"status\":\"" + order.getStatus() + "\"}";

            kafkaProducerService.sendOrderCreated(message);
        }

        return order;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));
    }

    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order cancelOrder(Long orderId) {

        Order order = getOrder(orderId);

        order.setStatus("CANCELLED");

        return orderRepository.save(order);
    }
}