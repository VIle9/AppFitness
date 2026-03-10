package MealFood;

import Food.FoodModel;
import Meal.MealModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_mealfood")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
