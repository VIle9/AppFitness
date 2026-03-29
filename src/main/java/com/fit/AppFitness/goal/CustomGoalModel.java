package com.fit.AppFitness.goal;

import com.fit.AppFitness.goal.enums.GoalFrequency;
import com.fit.AppFitness.goal.enums.GoalType;
import com.fit.AppFitness.goal.enums.GoalStatus;
import com.fit.AppFitness.goal.enums.GoalUnit;
import com.fit.AppFitness.user.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_custom_goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomGoalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double targetValue;
    private Double currentValue = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalUnit units;

    @Column(nullable = false)
    private Boolean notifyUser;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    private GoalFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Double getProgressPercentage(){
        if(targetValue == null || targetValue == 0){
            return 0.0;
        }
        double progress = (currentValue != null ? currentValue : 0.0) / targetValue * 100;
        return Math.min(progress, 100.0);
    }
}

