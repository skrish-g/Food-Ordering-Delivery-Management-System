package com.pwioi.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pwioi.app.dto.FoodResponse;

@FeignClient(
	    name = "food-service",
	    url = "http://localhost:8082",
	    configuration = com.pwioi.app.config.FeignConfig.class
	)
public interface FoodClient {

    @GetMapping("/foods/{id}")
    FoodResponse getFood(@PathVariable Long id);
}