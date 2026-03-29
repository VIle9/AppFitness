package com.fit.AppFitness.favorite;

import com.fit.AppFitness.security.CustomUserDetailsService;
import com.fit.AppFitness.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteFoodController {

    private final FavoriteFoodService favoriteFoodService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/{foodId}")
    public ResponseEntity<FavoriteFoodModel> addFavorite(
            Authentication authentication,
            @PathVariable Long foodId,
            @RequestParam(required = false) String notes){
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        FavoriteFoodModel favorite = favoriteFoodService.addFavorite(userId, foodId, notes);
        return ResponseEntity.ok(favorite);
    }

    @GetMapping
    public ResponseEntity<List<FavoriteFoodModel>> getAllFavorites(@AuthenticationPrincipal UserModel user){
        List<FavoriteFoodModel> favorites = favoriteFoodService.getAllFavorites(user.getId());
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> getFavoriteIds(@AuthenticationPrincipal UserModel user){
        List<Long> ids = favoriteFoodService.getFavoriteFoodIds(user.getId());
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/favorites/{foodId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long foodId){
        boolean isFavorite = favoriteFoodService.isFavorite(user.getId(), foodId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long foodId){
        favoriteFoodService.removeFavorite(user.getId(), foodId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/toggle/{foodId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long foodId,
            @RequestParam(required = false) String notes){
        FavoriteFoodModel result = favoriteFoodService.toggleFavorite(user.getId(), foodId, notes);

        Map<String, Object> response = new HashMap<>();
        response.put("isFavorite", result != null);
        response.put("favorite", result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{foodId}/notes")
    public ResponseEntity<FavoriteFoodModel> updateNotes(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long foodId,
            @RequestParam String notes){
        FavoriteFoodModel updated = favoriteFoodService.updateNotes(user.getId(), foodId, notes);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal UserModel user){
        Map<String, Object> stats = favoriteFoodService.getFavoriteStats(user.getId());
        return ResponseEntity.ok(stats);
    }

}
