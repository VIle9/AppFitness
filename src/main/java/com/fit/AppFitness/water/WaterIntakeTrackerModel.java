package com.fit.AppFitness.water;

import com.fit.AppFitness.user.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_water_intake", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntakeTrackerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @NotNull(message = "Quantidade é obrigatório.")
    @Positive(message = "Quantidade deve ser positivo.")
    @Column(nullable = false)
    private Double amountMl;

    @NotNull(message = "Data é obrigatório.")
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "goal_ml")
    private Double goalMl;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if(goalMl == null){
            goalMl = 2000.0;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    public Double getProgressPercentage(){
        if(goalMl == null || goalMl == 0){
            return 0.0;
        }
        return(amountMl / goalMl) * 100;
    }
}
