package com.fit.AppFitness.meal;

import com.fit.AppFitness.foods.FoodModel;
import com.fit.AppFitness.foods.FoodRepository;
import com.fit.AppFitness.mealfood.MealFoodModel;
import com.fit.AppFitness.user.UserModel;
import com.fit.AppFitness.user.UserService;
import com.fit.AppFitness.dto.MealDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private MealRepository mealRepository;
    private FoodRepository foodRepository;
    private UserService userService;

    public MealDTO.MealResponse createMeal(MealDTO.CreateMealRequest request){
        UserModel currentUser = userService.getCurrentUser();

        MealModel meal = new MealModel();
        meal.setUser(currentUser);
        meal.setName(request.getName());
        meal.setDate(request.getDate());
        meal.setMealType(request.getMealType());

        for(MealDTO.MealFoodRequest foodRequest : request.getFoods()){
            FoodModel food = foodRepository.findById(foodRequest.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Alimento não encontrado: " + foodRequest.getFoodId()));

            MealFoodModel mealFood = new MealFoodModel();
            mealFood.setMeal(meal);
            mealFood.setFood(food);
            mealFood.setQuantity(foodRequest.getQuantity());
            mealFood.setNotes(foodRequest.getNotes());

            meal.getFoods().add(mealFood);
        }

        MealModel savedMeal = mealRepository.save(meal);
        return mapToResponse(savedMeal);
    }

    public List<MealDTO.MealResponse> getMealsByDate(LocalDate date){
        UserModel currentUser = userService.getCurrentUser();
        List<MealModel> meals = mealRepository.findByUserIdAndDate(currentUser.getId(), date);
        return meals.stream().
                map(this::mapToResponse).
                collect(Collectors.toList());
    }

    public MealDTO.DailySummaryResponse getDailySummary(LocalDate date){
        UserModel currentUser = userService.getCurrentUser();
        List<MealModel> meals = mealRepository.findByUserIdAndDate(currentUser.getId(), date);

        MealDTO.DailySummaryResponse summary = new MealDTO.DailySummaryResponse();
        summary.setDate(date);
        summary.setCalorieGoal(currentUser.getDailyCalorieGoal());
        summary.setProteinGoal(currentUser.getProteinGoal());
        summary.setCarbGoals(currentUser.getCarbsGoal());
        summary.setFatGoals(currentUser.getFatGoal());

        int totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;

        List<MealDTO.MealResponse> mealResponses = new ArrayList<>();

        for(MealModel meal : meals){
            totalCalories += meal.getTotalCalories();
            totalProtein += meal.getTotalProtein();
            totalCarbs += meal.getTotalCarbs();
            totalFat += meal.getTotalFat();
            mealResponses.add(mapToResponse(meal));
        }

        summary.setTotalCalories(totalCalories);
        summary.setTotalProtein(totalProtein);
        summary.setTotalCarbs(totalCarbs);
        summary.setTotalFat(totalFat);
        summary.setMeals(mealResponses);

        return summary;
    }

    public void deleteMeal(Long mealId){
        MealModel meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada."));

        UserModel currentUser = userService.getCurrentUser();
        UserModel mealOwner = (UserModel) meal.getUser();

        if (!mealOwner.getId().equals(currentUser.getId())) {
            throw new RuntimeException("Você não tem permissão para deletar esta refeição");
        }

        mealRepository.delete(meal);
    }

    public MealDTO.MealResponse mapToResponse(MealModel meal){
        MealDTO.MealResponse response = new MealDTO.MealResponse();
        response.setId(meal.getId());
        response.setDate(meal.getDate());
        response.setMealType(meal.getMealType());
        response.setTotalCalories(meal.getTotalCalories());
        response.setTotalProteins(meal.getTotalCarbs());
        response.setTotalCarbs(meal.getTotalCarbs());
        response.setTotalFat(meal.getTotalFat());

        List<MealDTO.MealFoodItem> foodItems = meal.getFoods().stream()
                .map(mf -> {
                    MealDTO.MealFoodItem item = new MealDTO.MealFoodItem();
                    item.setFoodId(mf.getFood().getId());
                    item.setFoodName(mf.getFood().getName());
                    item.setQuantity(mf.getQuantity());

                    double multiplier = mf.getQuantity() / mf.getFood().getServingSize();
                    item.setCalories((int) (mf.getFood().getCalories() * multiplier));
                    item.setProtein(mf.getFood().getProtein() * multiplier);
                    item.setCarbs(mf.getFood().getCarbs() * multiplier);
                    item.setFat(mf.getFood().getFat() * multiplier);

                    return item;
                })
                .collect(Collectors.toList());

        response.setFoods(foodItems);
        return response;
    }

    @Transactional
    public MealModel copyMeal(Long userId, Long mealId, LocalDate newDate){
        MealModel originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Refeição não encontrada."));

        if(!originalMeal.getUser().getId().equals(userId)){
            throw new RuntimeException("Não autorizado.");
        }

        MealModel newMeal = new MealModel();
        newMeal.setUser(originalMeal.getUser());
        newMeal.setName(originalMeal.getName() + " (cópia)");
        newMeal.setDate(originalMeal.getDate());
        newMeal.setMealType(originalMeal.getMealType());
        newMeal.setNotes(originalMeal.getNotes());

        if(originalMeal.getFoods() != null && !originalMeal.getFoods().isEmpty()){
            Set<MealFoodModel> copiedFoods = new HashSet<>();

            for(MealFoodModel originalMealFood : originalMeal.getFoods()){
                MealFoodModel newMealFood = new MealFoodModel();
                newMealFood.setMeal(newMeal);
                newMealFood.setFood(originalMealFood.getFood());
                newMealFood.setQuantity(originalMealFood.getQuantity());
                copiedFoods.add(newMealFood);
            }
            newMeal.setFoods(copiedFoods);
        }
        return mealRepository.save(newMeal);
    }
}
