package com.fit.AppFitness.water;

import com.fit.AppFitness.dto.WaterIntakeTrackerDTO;
import com.fit.AppFitness.security.CustomUserDetailsService;
import com.fit.AppFitness.user.UserModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/water")
@RequiredArgsConstructor
@Tag(name = "Water Intake", description = "Endpoints para controle de consumo de água")
public class WaterIntakeTrackerController {

    private final WaterIntakeTrackerService waterIntakeTrackerService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping
    public ResponseEntity<WaterIntakeTrackerModel> addWaterIntake(
            Authentication authentication,
            @Valid @RequestBody WaterIntakeTrackerDTO dto){

        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        WaterIntakeTrackerModel created = waterIntakeTrackerService.addWaterIntake(userId, dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/today")
    public ResponseEntity<WaterIntakeTrackerModel> getTodayIntake(@AuthenticationPrincipal UserModel user){
        WaterIntakeTrackerModel today = waterIntakeTrackerService.getTodayIntake(user.getId());
        return ResponseEntity.ok(today);
    }

    @GetMapping
    public ResponseEntity<List<WaterIntakeTrackerModel>> getWaterIntakeHistory(
            @AuthenticationPrincipal UserModel user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        List<WaterIntakeTrackerModel> history = waterIntakeTrackerService.getWaterIntakeHistory(user.getId(), startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/status")
    public  ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal UserModel user){
        Map<String, Object> stats = waterIntakeTrackerService.getStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterIntakeTrackerModel> updateWaterIntake(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long id,
            @Valid @RequestBody WaterIntakeTrackerDTO dto){
        WaterIntakeTrackerModel updated = waterIntakeTrackerService.updateWaterIntake(user.getId(), id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterIntake(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long id){
        waterIntakeTrackerService.deleteWaterIntake(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

}
