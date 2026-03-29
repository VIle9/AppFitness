package com.fit.AppFitness.dto;

import com.fit.AppFitness.goal.enums.GoalFrequency;
import com.fit.AppFitness.goal.enums.GoalStatus;
import com.fit.AppFitness.goal.enums.GoalType;
import com.fit.AppFitness.goal.enums.GoalUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomGoalDTO {

    @NotBlank(message = "Nome é obrigatório.")
    private String name;
    private String description;

    @NotNull(message = "Valor alvo é obrigatório.")
    @Positive(message = "Valor alvo tem que ser positivo.")
    private Double targetValue;
    private Double currentValue;

    @NotNull(message = "Unidade é obrigatório.")
    private GoalUnit units;
    private Boolean notifyUser = true;
    private Boolean active = true;
    private GoalFrequency frequency;

    @NotNull(message = "Tipo de meta é obrigatório.")
    private GoalType type;
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @NotNull(message = "Data de inicio é obrigatório.")
    private LocalDate startDate;
    private LocalDate endDate;
}
