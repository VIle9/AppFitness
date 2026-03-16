package com.fit.AppFitness.meal;

import com.fit.AppFitness.meal.enums.MealType;
import com.fit.AppFitness.mealfood.MealFoodModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fit.AppFitness.user.UserModel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_meal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MealFoodModel> foods = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Integer getTotalCalories() {
        return foods.stream()
                .mapToInt(mf -> {
                    double multiplier = mf.getQuantity() / mf.getFood().getServingSize();
                    return (int) (mf.getFood().getCalories() * multiplier);
                })
                .sum();
    }

    public Double getTotalProtein() {
        return foods.stream()
                .mapToDouble(mf -> {
                    double multiplier = mf.getQuantity() / mf.getFood().getServingSize();
                    return mf.getFood().getProtein() * multiplier;
                })
                .sum();
    }

    public Double getTotalCarbs() {
        return foods.stream()
                .mapToDouble(mf -> {
                    double multiplier = mf.getQuantity() / mf.getFood().getServingSize();
                    return mf.getFood().getCarbs() * multiplier;
                })
                .sum();
    }

    public Double getTotalFat() {
        return foods.stream()
                .mapToDouble(mf -> {
                    double multiplier = mf.getQuantity() / mf.getFood().getServingSize();
                    return mf.getFood().getFat() * multiplier;
                })
                .sum();
    }

}
