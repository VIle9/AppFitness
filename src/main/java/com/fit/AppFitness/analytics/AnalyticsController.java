package com.fit.AppFitness.analytics;

import com.fit.AppFitness.security.CustomUserDetailsService;
import com.fit.AppFitness.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/advanced")
    public ResponseEntity<Map<String, Object>> getAdvancedAnalytics(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        Map<String, Object> analytics = analyticsService.getAdvancedAnalytics(userId, startDate, endDate);

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/week")
    public ResponseEntity<Map<String, Object>> getWeeklyAnalytics(Authentication authentication){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        Map<String, Object> analytics = analyticsService.getAdvancedAnalytics(userId, startDate, endDate);

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/month")
    public ResponseEntity<Map<String, Object>> getMonthlyAnalytics(Authentication authentication){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);

        Map<String, Object> analytics = analyticsService.getAdvancedAnalytics(userId, startDate, endDate);

        return ResponseEntity.ok(analytics);
    }
}
