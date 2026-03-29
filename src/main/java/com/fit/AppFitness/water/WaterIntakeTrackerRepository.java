package com.fit.AppFitness.water;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaterIntakeTrackerRepository extends JpaRepository<WaterIntakeTrackerModel, Long> {

    Optional<WaterIntakeTrackerModel> findByUserIdAndDate(Long userId, LocalDate date);

    List<WaterIntakeTrackerModel> findByUserIdOrderByDateDesc(Long userId);

    List<WaterIntakeTrackerModel> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(w.amountMl) FROM WaterIntakeTrackerModel w WHERE w.user.id = :userId")
    Double getAverageIntake(@Param("userId") Long userId);

    @Query("SELECT COUNT(w) FROM WaterIntakeTrackerModel w WHERE w.user.id = :userId AND w.amountMl >= w.goalMl")
    Long countGoalsReached(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
