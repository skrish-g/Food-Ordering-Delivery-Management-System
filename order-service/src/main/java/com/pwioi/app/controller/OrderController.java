package com.pwioi.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pwioi.app.dto.OrderRequest;
import com.pwioi.app.entity.Order;
import com.pwioi.app.service.OrderManagementService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderManagementService orderService;

    public OrderController(OrderManagementService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getCustomerOrders(
            @PathVariable Long customerId) {
        return orderService.getCustomerOrders(customerId);
    }

    @PutMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}