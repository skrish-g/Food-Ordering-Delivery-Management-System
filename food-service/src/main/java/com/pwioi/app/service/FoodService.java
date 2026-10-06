package com.pwioi.app.service;

import com.pwioi.app.entity.Food;
import com.pwioi.app.repository.FoodRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food addFood(Food food) {
        return foodRepository.save(food);
    }

    @Cacheable(value = "foods", key = "#id")
    public Food getFood(Long id) {
        System.out.println("CACHE MISS -> Fetching Food " + id + " from MySQL");
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }

    public List<Food> getAllFood() {
        return foodRepository.findAll();
    }

    @CacheEvict(value = "foods", key = "#id")
    public Food updateFood(Long id, Food updatedFood) {

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        food.setFoodName(updatedFood.getFoodName());
        food.setPrice(updatedFood.getPrice());
        food.setCategory(updatedFood.getCategory());
        food.setAvailability(updatedFood.isAvailability());
        food.setRestaurantId(updatedFood.getRestaurantId());

        return foodRepository.save(food);
    }

    @CacheEvict(value = "foods", key = "#id")
    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}