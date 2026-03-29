package com.fit.AppFitness.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class WeightHistoryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWeightRequest{

        @NotNull(message = "Peso é obrigatório.")
        @Positive(message = "Peso deve ser positivo.")
        private Double weight;

        @NotNull(message = "Data é obrigatório.")
        private LocalDate date;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateWeightRequest{

        @NotNull(message = "Peso é obrigatório.")
        @Positive(message = "Peso deve ser positivo.")
        private Double weight;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeightResponse{
        private Long id;
        private Double Weight;
        private LocalDate date;
        private String notes;
        private String createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeightStatsResponse{
        private Double currentWeight;
        private Double startWeight;
        private Double weightChange;
        private Double averageWeight;
        private Integer totalRecord;
        private LocalDate lastWeightDate;
    }
}
