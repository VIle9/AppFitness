package dto;

import user.Enums.UserActivityLevel;
import user.Enums.UserGender;
import user.Enums.UserGoal;
import user.Enums.UserSubscriptionStatus;
import lombok.Data;

import java.time.LocalDate;

public class UserDTO {

    @Data
    public static class UserProfileResponse {
        private Long id;
        private String email;
        private String password;
        private String name;
        private Double weight;
        private Double height;
        private LocalDate birthDate;
        private UserGender gender;
        private UserActivityLevel activityLevel;
        private UserGoal goal;
        private Integer dailyCalorieGoal;
        private Integer proteinGoal;
        private Integer carbsGoal;
        private Integer fatGoal;
        private UserSubscriptionStatus subscriptionStatus;
    }

    @Data
    public static class UpdateProfileRequest {
        private String name;
        private Double weight;
        private Double height;
        private LocalDate birthDate;
        private UserGender gender;
        private UserActivityLevel activityLevel;
        private UserGoal goal;
    }

    @Data
    public static class UpdateGoalRequest {
        private UserGoal goal;
        private UserActivityLevel activityLevel;
    }
}
