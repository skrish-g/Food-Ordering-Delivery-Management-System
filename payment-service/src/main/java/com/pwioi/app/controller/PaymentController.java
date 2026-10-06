package com.pwioi.app.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.*;

import com.pwioi.app.entity.Payment;
import com.pwioi.app.repository.PaymentRepository;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public Payment processPayment(@RequestBody Payment payment) {

        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("SUCCESS");

        return paymentRepository.save(payment);
    }
}