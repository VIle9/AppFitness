package com.fit.AppFitness.meal;

import com.fit.AppFitness.dto.MealDTO;
import com.fit.AppFitness.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping
    public ResponseEntity<MealDTO.MealResponse> createMeal(@Valid @RequestBody MealDTO.CreateMealRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(mealService.createMeal(request));
    }

    @GetMapping
    public ResponseEntity<List<MealDTO.MealResponse>> getMealsByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date){
        return ResponseEntity.ok(mealService.getMealsByDate(date));
    }

    @GetMapping("/summary")
    public ResponseEntity<MealDTO.DailySummaryResponse> getDailySummary(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date){
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(mealService.getDailySummary(targetDate));
    }

    @PostMapping("/{mealId}/copy")
    public ResponseEntity<MealDTO.MealResponse> copyMeal(
            Authentication authentication,
            @PathVariable Long mealId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        MealModel copiedMeal = mealService.copyMeal(userId, mealId, newDate);
        return ResponseEntity.ok(mealService.mapToResponse(copiedMeal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id){
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
