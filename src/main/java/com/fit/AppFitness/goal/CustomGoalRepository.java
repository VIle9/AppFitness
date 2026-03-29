package com.fit.AppFitness.goal;

import com.fit.AppFitness.goal.enums.GoalStatus;
import com.fit.AppFitness.goal.enums.GoalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CustomGoalRepository extends JpaRepository<CustomGoalModel, Long> {

    List<CustomGoalModel> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CustomGoalModel> findByUserIdAndActiveOrderByCreatedAtDesc(Long userId, Boolean active);

    List<CustomGoalModel> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, GoalStatus status);

    List<CustomGoalModel> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, GoalType type);

    @Query("SELECT g FROM CustomGoalModel g WHERE g.user.id = :userId " +
            "AND g.startDate <= :date AND (g.endDate IS NULL OR g.endDate >= :date)")
    List<CustomGoalModel> findActiveGoalsByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(g) FROM CustomGoalModel g WHERE g.user.id = :userId AND g.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") GoalStatus status);

    @Query("SELECT g FROM CustomGoalModel g WHERE g.user.id = :userId AND g.active = true " +
            "AND g.endDate IS NOT NULL AND g.endDate < :today AND g.status = 'IN_PROGRESS'")
    List<CustomGoalModel> findExpiredGoals(@Param("userId") Long userId, @Param("today") LocalDate today);
}
