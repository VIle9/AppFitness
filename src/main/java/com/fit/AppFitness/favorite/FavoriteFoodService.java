package com.fit.AppFitness.favorite;

import com.fit.AppFitness.foods.FoodModel;
import com.fit.AppFitness.foods.FoodRepository;
import com.fit.AppFitness.user.UserModel;
import com.fit.AppFitness.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteFoodService {

    private final FavoriteFoodRepository favoriteFoodRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Transactional
    public FavoriteFoodModel addFavorite(Long userId, Long foodId, String notes){
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        FoodModel food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Comida não encontrada"));

        if(favoriteFoodRepository.existsByUserIdAndFoodId(userId, foodId)){
            throw new RuntimeException("Comida já está nos favoritos.");
        }

        FavoriteFoodModel favorite = new FavoriteFoodModel();
        favorite.setUser(user);
        favorite.setFood(food);
        favorite.setNotes(notes);

        return favoriteFoodRepository.save(favorite);
    }

    public List<FavoriteFoodModel> getAllFavorites(Long userId){
        return favoriteFoodRepository.findByUserIdOrderByFavoritedAtDesc(userId);
    }

    public boolean isFavorite(Long userId, Long foodId){
        return favoriteFoodRepository.existsByUserIdAndFoodId(userId, foodId);
    }

    public List<Long> getFavoriteFoodIds(Long userId){
        return favoriteFoodRepository.findFavoriteFoodIdsByUserId(userId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long foodId){
        if(!favoriteFoodRepository.existsByUserIdAndFoodId(userId, foodId)){
            throw new RuntimeException("Comida não está nos favoritos.");
        }
        favoriteFoodRepository.deleteByUserIdAndFoodId(userId, foodId);
    }

    @Transactional
    public FavoriteFoodModel toggleFavorite(Long userId, Long foodId, String notes){
        if(favoriteFoodRepository.existsByUserIdAndFoodId(userId, foodId)) {
            favoriteFoodRepository.existsByUserIdAndFoodId(userId, foodId);
            return null;
        }else{
            return addFavorite(userId, foodId, notes);
        }
    }

    @Transactional
    public FavoriteFoodModel updateNotes(Long userId, Long foodId, String notes){
        FavoriteFoodModel favorite = favoriteFoodRepository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new RuntimeException("Comida não encontrada."));

        favorite.setNotes(notes);
        return favoriteFoodRepository.save(favorite);
    }

    public Map<String, Object> getFavoriteStats(Long userId){
        Map<String, Object> stats = new HashMap<>();

        long total = favoriteFoodRepository.countByUserId(userId);
        stats.put("totalFavorites", total);

        List<FavoriteFoodModel> favorites = favoriteFoodRepository.findByUserIdOrderByFavoritedAtDesc(userId);

        if(!favorites.isEmpty()){
            double avgCalories = favorites.stream()
                    .mapToDouble(f -> f.getFood().getCalories())
                    .average()
                    .orElse(0.0);

            stats.put("averageCalories", avgCalories);

            FavoriteFoodModel highestCalorie = favorites.stream()
                    .max((f1, f2) -> Double.compare(f1.getFood().getCalories(), f2.getFood().getCalories()))
                    .orElse(null);

            if(highestCalorie != null){
                stats.put("highestCalorieFood", highestCalorie.getFood().getName());
                stats.put("highestCalories", highestCalorie.getFood().getCalories());
            }

            FavoriteFoodModel lowestCalorie = favorites.stream()
                    .min((f1, f2) -> Double.compare(f1.getFood().getCalories(), f2.getFood().getCalories()))
                    .orElse(null);

            if(lowestCalorie != null){
                stats.put("lowestCalorieFood", lowestCalorie.getFood().getName());
                stats.put("lowestCalories", lowestCalorie.getFood().getCalories());
            }
        }

        return stats;
    }

}
