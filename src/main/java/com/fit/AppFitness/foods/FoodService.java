package com.fit.AppFitness.foods;

import com.fit.AppFitness.foods.enums.FoodType;
import com.fit.AppFitness.user.UserModel;
import com.fit.AppFitness.user.UserService;
import com.fit.AppFitness.dto.FoodDTO;
import com.fit.AppFitness.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserService userService;

    public List<FoodDTO.FoodResponse> getAllFoods(){
        return foodRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FoodDTO.FoodResponse> searchFoods(String query){
        return foodRepository.searchByNameOrBrand(query).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FoodDTO.FoodResponse getFoodById(Long id){
        FoodModel food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alimento não encontrado."));
        return mapToResponse(food);
    }

    public FoodDTO.FoodResponse createFood(FoodDTO.CreateFoodRequest request){
        UserModel currentUser = userService.getCurrentUser();

        FoodModel food = new FoodModel();
        food.setName(request.getName());
        food.setBrand(request.getBrand());
        food.setServingSize(request.getServingSize());
        food.setUnit(request.getUnit());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setCarbs(request.getCarbs());
        food.setFat(request.getFat());
        food.setFiber(request.getFiber());
        food.setSugar(request.getSugar());
        food.setFoodType(FoodType.CUSTOM);
        food.setCreatedBy(currentUser);

        FoodModel savedFood = foodRepository.save(food);
        return mapToResponse(savedFood);
    }

    public void deleteFood(Long id){
        FoodModel food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alimento não encontrado."));

        UserModel currentUser = userService.getCurrentUser();
        UserModel creator = (UserModel) food.getCreatedBy();

        if(creator == null){
            throw new RuntimeException("Não é possivel deletar alimentos do sistema.");
        }

        if (!creator.getId().equals(currentUser.getId())) {
            throw new RuntimeException("Você não tem permissão para deletar este alimento");
        }

        foodRepository.delete(food);
    }

    private FoodDTO.FoodResponse mapToResponse(FoodModel food){
        FoodDTO.FoodResponse response = new FoodDTO.FoodResponse();
        response.setId(food.getId());
        response.setName(food.getName());
        response.setBrand(food.getBrand());
        response.setServingSize(food.getServingSize());
        response.setUnit(food.getUnit());
        response.setCalories(food.getCalories());
        response.setProtein(food.getProtein());
        response.setFat(food.getFat());
        response.setFiber(food.getFiber());
        response.setSugar(food.getSugar());
        response.setFoodType(food.getFoodType());
        return response;
    }
}
