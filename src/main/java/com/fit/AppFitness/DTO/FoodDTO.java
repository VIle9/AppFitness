package com.fit.AppFitness.DTO;

import Food.Enums.FoodType;
import Food.Enums.FoodUnit;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    public static class createFoodRequest{

        @NotBlank(message = "Nome é obrigatório")
        private String name;
        private String brand;

        @NotBlank(message = "Tamanho da porção é obrigatório")
        private Double servingSize;
        private FoodUnit unit = FoodUnit.GRAMS;

        @NotBlank(message = "Calorias são obrigatórias")
        private Integer calories;

        @NotBlank(message = "Proteinas são obrigatórias")
        private Double protein;

        @NotBlank(message = "Carboidratos são obrigatórios")
        private Double carbs;

        @NotBlank(message = "Gordura é obrigatórias")
        private Double fat;

        private Double fiber;
        private Double sugar;

    }
}
