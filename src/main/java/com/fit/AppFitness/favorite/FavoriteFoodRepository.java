package com.fit.AppFitness.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteFoodRepository extends JpaRepository<FavoriteFoodModel, Long> {

    List<FavoriteFoodModel> findByUserIdOrderByFavoritedAtDesc(Long userId);

    Optional<FavoriteFoodModel> findByUserIdAndFoodId(Long userId, Long foodId);

    boolean existsByUserIdAndFoodId(Long userId, Long foodId);

    void deleteByUserIdAndFoodId(Long userId, Long foodId);

    long countByUserId(Long userId);

    @Query("SELECT f.food.id FROM FavoriteFoodModel f WHERE f.user.id = :userId")
    List<Long> findFavoriteFoodIdsByUserId(@Param("userId") Long userId);
}
