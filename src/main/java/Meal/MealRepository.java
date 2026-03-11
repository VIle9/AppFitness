package Meal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<MealModel, Long> {

    List<MealModel> findByUserIdAndDate(Long userId, LocalDate date);

    List<MealModel> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<MealModel> findByUserIdOrderByDateDesc(Long userId);
}
