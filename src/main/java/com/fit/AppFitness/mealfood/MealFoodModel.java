package com.fit.AppFitness.mealfood;

import com.fit.AppFitness.foods.FoodModel;
import com.fit.AppFitness.meal.MealModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_mealfood")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"meal", "food"})
@EqualsAndHashCode(exclude = {"meal", "food"})
public class MealFoodModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_id", nullable = false)
    private MealModel meal;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private FoodModel food;

    @Column(nullable = false)
    private Double quantity;

    private String notes;
}
