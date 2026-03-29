package com.fit.AppFitness.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntakeTrackerDTO {

    @NotNull(message = "Quantidade é obrigatório.")
    @Positive(message = "Quantidade tem que ser positivo.")
    private Double amountMl;

    @NotNull(message = "Data é obrigatório.")
    @PastOrPresent(message = "Data não pode estar no futuro.")
    private LocalDate date;

    @Positive(message = "Meta deve ser positiva.")
    private Double goalMl;
    private String notes;
}
