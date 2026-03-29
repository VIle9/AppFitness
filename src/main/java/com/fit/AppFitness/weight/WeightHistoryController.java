package com.fit.AppFitness.weight;

import com.fit.AppFitness.dto.WeightHistoryDTO;
import com.fit.AppFitness.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/weight")
public class WeightHistoryController {

    private final WeightHistoryService weightHistoryService;
    private final CustomUserDetailsService userDetailsService;

    public WeightHistoryController(WeightHistoryService weightHistoryService, CustomUserDetailsService userDetailsService) {
        this.weightHistoryService = weightHistoryService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public ResponseEntity<WeightHistoryDTO.WeightResponse> addWeight(
            @Valid @RequestBody WeightHistoryDTO.CreateWeightRequest request,
            Authentication authentication){
        Long userId = userDetailsService.getUserIdFromAuthentication(authentication);
        WeightHistoryDTO.WeightResponse response = weightHistoryService.addWeight(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeightHistoryDTO.WeightResponse> updateWeight(
            @PathVariable Long id,
            @Valid @RequestBody WeightHistoryDTO.UpdateWeightRequest request,
            Authentication authentication){
        Long userId = userDetailsService.getUserIdFromAuthentication(authentication);
        WeightHistoryDTO.WeightResponse response = weightHistoryService.updateWeight(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/period")
    public ResponseEntity<List<WeightHistoryDTO.WeightResponse>> getHistoryByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication){
        Long userId = userDetailsService.getUserIdFromAuthentication(authentication);
        List<WeightHistoryDTO.WeightResponse> history =
                weightHistoryService.getHistoryByPeriod(userId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/stats")
    public ResponseEntity<WeightHistoryDTO.WeightStatsResponse> getStats(
            Authentication authentication){
        Long userId = userDetailsService.getUserIdFromAuthentication(authentication);
        WeightHistoryDTO.WeightStatsResponse stats = weightHistoryService.getStats(userId);
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeight(
            @PathVariable Long id,
            Authentication authentication){
        Long userId = userDetailsService.getUserIdFromAuthentication(authentication);
        weightHistoryService.deleteWeight(userId, id);
        return ResponseEntity.noContent().build();
    }
}
