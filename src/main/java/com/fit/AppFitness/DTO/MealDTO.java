package com.fit.AppFitness.DTO;

import Meal.Enum.MealType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class MealDTO {

    @Data
    public static class mealResponse{
        private Long id;
        private LocalDate date;
        private MealType mealType;
        private List<mealFoodItem> foods;
        private Integer totalCalories;
        private Double totalProteins;
        private Double totalCarbs;
        private Double totalFat;
    }

    @Data
    public static class mealFoodItem{
        private Long foodId;
        private String foodName;
        private Double quantity;
        private Integer calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }

    @Data
    public static class createMealRequest{

        @NotBlank(message = "Data é obrigatório")
        private LocalDate date;

        @NotBlank(message = "Tipo de refeição é obrigatório")
        private MealType mealType;

        @NotBlank(message = "Alimentos são obrigatórios")
        private List<mealFoodRequest> foods;
    }

    @Data
    public static class mealFoodRequest{

        @NotBlank(message = "ID da comida é obrigatório")
        private Long foodId;

        @NotBlank(message = "Quantidade é obrigatório")
        private Double quantity;
        private String notes;
    }

    @Data
    public static class dailySummaryResponse{
        private LocalDate date;
        private Integer totalCalories;
        private Double totalProtein;
        private Double totalCarbs;
        private Double totalFat;
        private Integer calorieGoal;
        private Integer proteinGoal;
        private Integer carbGoals;
        private Integer fatGoals;
        private List<mealResponse> meals;
    }
}
