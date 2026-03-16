package com.fit.AppFitness.user;

import com.fit.AppFitness.meal.MealModel;
import com.fit.AppFitness.user.Enums.UserActivityLevel;
import com.fit.AppFitness.user.Enums.UserGender;
import com.fit.AppFitness.user.Enums.UserGoal;
import com.fit.AppFitness.user.Enums.UserSubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;
    private Double weight;
    private Double height;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Enumerated(EnumType.STRING)
    private UserActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private UserGoal goal;
    private Integer dailyCalorieGoal;
    private Integer proteinGoal;
    private Integer carbsGoal;
    private Integer fatGoal;

    @Enumerated(EnumType.STRING)
    private UserSubscriptionStatus subscriptionStatus = UserSubscriptionStatus.FREE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<MealModel> meals = new HashSet<>();


}
