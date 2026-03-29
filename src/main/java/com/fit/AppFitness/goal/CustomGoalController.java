package com.fit.AppFitness.goal;

import com.fit.AppFitness.dto.CustomGoalDTO;
import com.fit.AppFitness.goal.enums.GoalStatus;
import com.fit.AppFitness.goal.enums.GoalType;
import com.fit.AppFitness.security.CustomUserDetailsService;
import com.fit.AppFitness.user.UserModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class CustomGoalController {

    private final CustomGoalService customGoalService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping
    public ResponseEntity<CustomGoalModel> createGoal(
            Authentication authentication,
            @Valid @RequestBody CustomGoalDTO dto){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        CustomGoalModel created = customGoalService.createdGoal(userId, dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<CustomGoalModel>> getAllGoals(@AuthenticationPrincipal UserModel user){
        List<CustomGoalModel> goals = customGoalService.getAllGoals(user.getId());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/activeGoals")
    public ResponseEntity<List<CustomGoalModel>> getActiveGoals(@AuthenticationPrincipal UserModel user){
        List<CustomGoalModel> goals = customGoalService.getActiveGoals(user.getId());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomGoalModel>>getGoalsByStatus(
            @AuthenticationPrincipal UserModel user,
            @PathVariable GoalStatus status){
        List<CustomGoalModel> goals = customGoalService.getGoalsByStatus(user.getId(), status);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CustomGoalModel>> getGoalsByType(
            @AuthenticationPrincipal UserModel user,
            @PathVariable GoalType type){
        List<CustomGoalModel> goals = customGoalService.getGoalsByType(user.getId(), type);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<CustomGoalModel> getGoalsById(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long goalId){
        CustomGoalModel goal = customGoalService.getGoalById(user.getId(), goalId);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<CustomGoalModel> updateGoal(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long goalId,
            @Valid @RequestBody CustomGoalDTO dto){
        CustomGoalModel updated = customGoalService.updateGoal(user.getId(), goalId, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{goalId}/progress")
    public ResponseEntity<CustomGoalModel> updatedProgress(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long userId,
            @RequestParam Double value){
        CustomGoalModel updated = customGoalService.updateProgress(user.getId(), userId, value);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{goalId}/toggle")
    public ResponseEntity<CustomGoalModel> toggleActive(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long goalId){
        CustomGoalModel updated = customGoalService.toggleActive(user.getId(), goalId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<CustomGoalModel> deleteGoal(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long goalId){
        customGoalService.deleteGoal(user.getId(), goalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal UserModel user){
        Map<String, Object> stats = customGoalService.getGoalStatus(user.getId());
        return ResponseEntity.ok(stats);
    }

    public ResponseEntity<Void> checkExpiredGoals(@AuthenticationPrincipal UserModel user){
        customGoalService.checkAndUpdateExpiredGoals(user.getId());
        return ResponseEntity.ok().build();
    }
}
