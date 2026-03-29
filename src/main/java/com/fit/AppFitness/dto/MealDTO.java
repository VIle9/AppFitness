package com.fit.AppFitness.dto;

import com.fit.AppFitness.meal.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class MealDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealResponse {
        private Long id;
        private String name;
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMealRequest {

        @NotNull(message = "Nome é obrigatório.")
        private String name;

        @NotNull(message = "Data é obrigatório")
        private LocalDate date;

        @NotNull(message = "Tipo de refeição é obrigatório")
        private MealType mealType;

        @NotNull(message = "Alimentos são obrigatórios")
        private List<MealFoodRequest> foods;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealFoodRequest {

        @NotNull(message = "ID da comida é obrigatório")
        @Positive(message = "ID deve ser positivo.")
        private Long foodId;

        @NotNull(message = "Quantidade é obrigatório")
        @Positive(message = "Quantidade tem que ser positiva.")
        private Double quantity;
        private String notes;
    }

    @Data
    public static class DailySummaryResponse {
        private String name;
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
