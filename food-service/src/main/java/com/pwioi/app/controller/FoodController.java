package com.pwioi.app.controller;

import com.pwioi.app.entity.Food;
import com.pwioi.app.service.FoodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping
    public Food addFood(@RequestBody Food food) {
        return foodService.addFood(food);
    }

    @GetMapping("/{id}")
    public Food getFood(@PathVariable Long id) {
        return foodService.getFood(id);
    }

    @GetMapping
    public List<Food> getAllFood() {
        return foodService.getAllFood();
    }

    @PutMapping("/{id}")
    public Food updateFood(
            @PathVariable Long id,
            @RequestBody Food food) {
        return foodService.updateFood(id, food);
    }

    @DeleteMapping("/{id}")
    public String deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return "Food deleted successfully";
    }
}