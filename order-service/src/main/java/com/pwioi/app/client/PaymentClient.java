package com.pwioi.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pwioi.app.dto.PaymentRequest;
import com.pwioi.app.dto.PaymentResponse;

@FeignClient(
	    name = "payment-service",
	    url = "http://localhost:8083",
	    configuration = com.pwioi.app.config.FeignConfig.class
	)
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}