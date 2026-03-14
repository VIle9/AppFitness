package com.fit.AppFitness.DTO;

import Meal.Enum.MealType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class MealDTO {

    @Data
    public static class MealResponse {
        private Long id;
        private LocalDate date;
        private MealType mealType;
        private List<MealFoodItem> foods;
        private Integer totalCalories;
        private Double totalProteins;
        private Double totalCarbs;
        private Double totalFat;
    }

    @Data
    public static class MealFoodItem {
        private Long foodId;
        private String foodName;
        private Double quantity;
        private Integer calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }

    @Data
    public static class CreateMealRequest {

        @NotBlank(message = "Data é obrigatório")
        private LocalDate date;

        @NotBlank(message = "Tipo de refeição é obrigatório")
        private MealType mealType;

        @NotBlank(message = "Alimentos são obrigatórios")
        private List<MealFoodRequest> foods;
    }

    @Data
    public static class MealFoodRequest {

        @NotBlank(message = "ID da comida é obrigatório")
        private Long foodId;

        @NotBlank(message = "Quantidade é obrigatório")
        private Double quantity;
        private String notes;
    }

    @Data
    public static class DailySummaryResponse {
        private LocalDate date;
        private Integer totalCalories;
        private Double totalProtein;
        private Double totalCarbs;
        private Double totalFat;
        private Integer calorieGoal;
        private Integer proteinGoal;
        private Integer carbGoals;
        private Integer fatGoals;
        private List<MealResponse> meals;
    }
}
