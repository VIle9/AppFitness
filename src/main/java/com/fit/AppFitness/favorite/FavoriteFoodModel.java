package com.fit.AppFitness.favorite;

import com.fit.AppFitness.foods.FoodModel;
import com.fit.AppFitness.user.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_favorite_food", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "food_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteFoodModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id", nullable = false)
    private FoodModel food;

    @Column(name = "favorited_at", nullable = false, updatable = false)
    private LocalDateTime favoritedAt;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate(){
        favoritedAt = LocalDateTime.now();
    }

}
