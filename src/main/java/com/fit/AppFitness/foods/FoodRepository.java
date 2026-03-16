package com.fit.AppFitness.foods;

import com.fit.AppFitness.foods.enums.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodModel, Long> {

    @Query("SELECT f FROM FoodModel f WHERE " +
    "LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    "LOWER(f.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<FoodModel> searchByNameOrBrand(@Param("searchTerm") String searchTerm);

    List<FoodModel> findByCreatedById(Long userId);

    List<FoodModel> findByFoodType(FoodType foodType);
}
