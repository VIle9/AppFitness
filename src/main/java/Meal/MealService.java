package Meal;

import Food.FoodModel;
import Food.FoodRepository;
import MealFood.MealFoodModel;
import User.UserModel;
import User.UserService;
import com.fit.AppFitness.DTO.MealDTO;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserService userService;

    public MealDTO.mealResponse createMeal(MealDTO.createMealRequest request){
        UserModel currentUser = userService.getCurrentUser();

        MealModel meal = new MealModel();
        meal.setUser((User) currentUser);
        meal.setDate(request.getDate());
        meal.setMealType(request.getMealType());

        for(MealDTO.mealFoodRequest foodRequest : request.getFoods()){
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

    public List<MealDTO.mealResponse> getMealsByDate(LocalDate date){
        UserModel currentUser = userService.getCurrentUser();
        List<MealModel> meals = mealRepository.findByUserIdAndDate(currentUser.getId(), date);
        return meals.stream().
                map(this::mapToResponse).
                collect(Collectors.toList());
    }

    public MealDTO.dailySummaryResponse getDailySummary(LocalDate date){
        UserModel currentUser = userService.getCurrentUser();
        List<MealModel> meals = mealRepository.findByUserIdAndDate(currentUser.getId(), date);

        MealDTO.dailySummaryResponse summary = new MealDTO.dailySummaryResponse();
        summary.setDate(date);
        summary.setCalorieGoal(currentUser.getDailyCalorieGoal());
        summary.setProteinGoal(currentUser.getProteinGoal());
        summary.setCarbGoals(currentUser.getCarbsGoal());
        summary.setFatGoals(currentUser.getFatGoal());

        int totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;

        List<MealDTO.mealResponse> mealResponses = new ArrayList<>();

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

    private MealDTO.mealResponse mapToResponse(MealModel meal){
        MealDTO.mealResponse response = new MealDTO.mealResponse();
        response.setId(meal.getId());
        response.setDate(meal.getDate());
        response.setMealType(meal.getMealType());
        response.setTotalCalories(meal.getTotalCalories());
        response.setTotalProteins(meal.getTotalCarbs());
        response.setTotalCarbs(meal.getTotalCarbs());
        response.setTotalFat(meal.getTotalFat());

        List<MealDTO.mealFoodItem> foodItems = meal.getFoods().stream()
                .map(mf -> {
                    MealDTO.mealFoodItem item = new MealDTO.mealFoodItem();
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
}
