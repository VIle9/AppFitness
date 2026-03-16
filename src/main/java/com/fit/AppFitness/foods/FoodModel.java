package com.fit.AppFitness.foods;

import com.fit.AppFitness.foods.enums.FoodType;
import com.fit.AppFitness.foods.enums.FoodUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fit.AppFitness.user.UserModel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_food")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String brand;
    private String barcode;

    @Column(nullable = false)
    private Double servingSize;

    @Enumerated(EnumType.STRING)
    private FoodUnit unit = FoodUnit.GRAMS;

    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private Double protein;

    @Column(nullable = false)
    private Double carbs;

    @Column(nullable = false)
    private Double fat;
    private Double fiber;
    private Double sugar;
    private Double sodium;

    @Enumerated(EnumType.STRING)
    private FoodType foodType = FoodType.CUSTOM;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
