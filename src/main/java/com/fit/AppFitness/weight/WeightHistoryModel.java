package com.fit.AppFitness.weight;

import com.fit.AppFitness.user.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_weight_history", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDate date;
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

}
