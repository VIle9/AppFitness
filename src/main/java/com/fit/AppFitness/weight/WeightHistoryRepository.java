package com.fit.AppFitness.weight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightHistoryRepository extends JpaRepository<WeightHistoryModel, Long> {

    List<WeightHistoryModel> findByUserIdOrderByDateDesc(Long userId);

    Optional<WeightHistoryModel> findByUserIdAndDate(Long userId, LocalDate date);

    Optional<WeightHistoryModel> findFirstByUserIdOrderByDateDesc(Long id);

    List<WeightHistoryModel> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
