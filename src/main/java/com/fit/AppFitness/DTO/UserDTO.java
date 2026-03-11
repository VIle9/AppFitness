package com.fit.AppFitness.DTO;

import User.Enums.UserActivityLevel;
import User.Enums.UserGender;
import User.Enums.UserGoal;
import User.Enums.UserSubscriptionStatus;
import User.UserModel;
import lombok.Data;
import org.apache.catalina.User;

import java.time.LocalDate;

public class UserDTO {

    @Data
    public static class userProfileResponse{
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
        private Integer dayliCalorieGoal;
        private Integer carbsGoal;
        private Integer fatGoal;
        private UserSubscriptionStatus subscriptionStatus;
    }

    @Data
    public static class updateProfileRequest{
        private String name;
        private Double weight;
        private Double height;
        private LocalDate birthDate;
        private UserGender gender;
        private UserActivityLevel activityLevel;
        private UserGoal goal;
    }

    @Data
    public static class updateGoalRequest{
        private UserGoal goal;
        private UserActivityLevel activityLevel;
    }
}
