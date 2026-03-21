package com.fit.AppFitness.dto;

import com.fit.AppFitness.foods.enums.FoodType;
import com.fit.AppFitness.foods.enums.FoodUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FoodDTO {

    @Data
    public static class FoodResponse{
        private Long id;
        private String name;
        private String brand;
        private Double servingSize;
        private FoodUnit unit;
        private Integer calories;
        private Double protein;
        private Double carbs;
        private Double fat;
        private Double fiber;
        private Double sugar;
        private FoodType foodType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFoodRequest {

        @NotBlank(message = "Nome é obrigatório")
        private String name;
        private String brand;

        @NotNull(message = "Tamanho da porção é obrigatório.")
        @Positive(message = "Tamanho da porção deve ser positiva.")
        private Double servingSize;
        private FoodUnit unit = FoodUnit.GRAMS;

        @NotNull(message = "Caloria é obrigatório.")
        private Integer calories;

        @NotNull(message = "Proteina é obrigatório.")
        private Double protein;

        @NotNull(message = "Carboidrato é obrigatório.")
        private Double carbs;

        @NotNull(message = "Gordura é obrigatório.")
        private Double fat;

        private Double fiber;
        private Double sugar;

    }
}
