package user;

import user.Enums.UserGender;
import dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserModel getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public UserDTO.UserProfileResponse getProfile(){
        UserModel user = getCurrentUser();
        return mapToProfileResponse(user);
    }

    public UserDTO.UserProfileResponse updateProfile(UserDTO.UpdateProfileRequest request){
        UserModel user = getCurrentUser();

        if(request.getName() != null) user.setName(request.getName());
        if(request.getWeight() != null) user.setWeight(request.getWeight());
        if(request.getHeight() != null) user.setHeight(request.getHeight());
        if(request.getBirthDate() != null) user.setBirthDate(request.getBirthDate());
        if(request.getGender() != null) user.setGender(request.getGender());
        if(request.getActivityLevel() != null) user.setActivityLevel(request.getActivityLevel());
        if(request.getGoal() != null) user.setGoal(request.getGoal());

        calculateAndSetGoals(user);

        UserModel savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    public UserDTO.UserProfileResponse updateGoals(UserDTO.UpdateProfileRequest request){
        UserModel user = getCurrentUser();

        if(request.getGoal() != null) user.setGoal(request.getGoal());
        if(request.getActivityLevel() != null) user.setActivityLevel(request.getActivityLevel());

        calculateAndSetGoals(user);

        UserModel savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    private void calculateAndSetGoals(UserModel user){
        if(user.getWeight() == null || user.getHeight() == null ||
                user.getBirthDate() == null || user.getGender() == null ||
                user.getActivityLevel() == null || user.getGoal() == null){
            return;
        }

        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();

        double bmr;
        if(user.getGender() == UserGender.MALE){
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age) * 5;
        }else{
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age) - 161;
        }

        double activityMultiplier = switch (user.getActivityLevel()){
            case SEDENTARY -> 1.2;
            case LIGHTLY_ACTIVE -> 1.375;
            case MODERATELY_ACTIVE -> 1.55;
            case VERY_ACTIVE -> 1.725;
            case EXTRA_ACTIVE -> 1.9;
        };

        double tdee = bmr * activityMultiplier;

        int dailyCalories = switch (user.getGoal()){
            case LOSE_WEIGHT -> (int) (tdee - 500);
            case MAINTAIN -> (int) tdee;
            case GAIN_WEIGHT -> (int) (tdee + 300);
        };

        user.setDailyCalorieGoal(dailyCalories);

        user.setProteinGoal((int) (dailyCalories * 0.30 / 4));
        user.setCarbsGoal((int) (dailyCalories * 0.40 / 4));
        user.setFatGoal((int) (dailyCalories * 0.30 / 9));
    }

    private UserDTO.UserProfileResponse mapToProfileResponse(UserModel user){
        UserDTO.UserProfileResponse response = new UserDTO.UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setWeight(user.getWeight());
        response.setHeight(user.getHeight());
        response.setBirthDate(user.getBirthDate());
        response.setGender(user.getGender());
        response.setActivityLevel(user.getActivityLevel());
        response.setGoal(user.getGoal());
        response.setDailyCalorieGoal(user.getDailyCalorieGoal());
        response.setProteinGoal(user.getProteinGoal());
        response.setCarbsGoal(user.getCarbsGoal());
        response.setFatGoal(user.getFatGoal());
        response.setSubscriptionStatus(user.getSubscriptionStatus());
        return response;
    }

}
