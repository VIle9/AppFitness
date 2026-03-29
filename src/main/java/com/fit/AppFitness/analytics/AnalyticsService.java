package com.fit.AppFitness.analytics;

import com.fit.AppFitness.meal.MealModel;
import com.fit.AppFitness.meal.MealRepository;
import com.fit.AppFitness.water.WaterIntakeTrackerModel;
import com.fit.AppFitness.water.WaterIntakeTrackerRepository;
import com.fit.AppFitness.weight.WeightHistoryModel;
import com.fit.AppFitness.weight.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MealRepository mealRepository;
    private final WaterIntakeTrackerRepository waterIntakeTrackerRepository;
    private final WeightHistoryRepository weightHistoryRepository;

    public Map<String, Object> getAdvancedAnalytics(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("nutritionalAnalysis", getNutitionalAnalysis(userId, startDate, endDate));
        analytics.put("trend", getTrendsAnalysis(userId, startDate, endDate));
        analytics.put("patterns", getPatternAnalysis(userId, startDate, endDate));
        analytics.put("progress", getProgressAnalysis(userId, startDate, endDate));
        analytics.put("comparative", getComparativeAnalysis(userId, startDate, endDate));
        analytics.put("recommendations", generateRecommendationsAnalysis(userId, startDate, endDate));
        analytics.put("healthScore", calculateHealthScore(userId, startDate, endDate));

        return analytics;
    }

    private Map<String, Object> getNutitionalAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        List<MealModel> meals = mealRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        Map<String, Object> analysis = new HashMap<>();

        if (meals.isEmpty()) {
            analysis.put("message", "Sem dados para o período.");
            return analysis;
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double totalCalories = meals.stream()
                .mapToDouble(MealModel::getTotalCalories).sum();
        double totalProtein = meals.stream()
                .mapToDouble(MealModel::getTotalProtein).sum();
        double totalCarbs = meals.stream()
                .mapToDouble(MealModel::getTotalCarbs).sum();
        double totalFat = meals.stream()
                .mapToDouble(MealModel::getTotalFat).sum();

        analysis.put("averageDailyCalories", totalCalories / days);
        analysis.put("averageDailyProtein", totalProtein / days);
        analysis.put("averageDailyCarbs", totalCarbs / days);
        analysis.put("averageDailyFat", totalFat / days);

        double totalMacros = totalProtein + totalCarbs + totalFat;
        if (totalMacros > 0) {
            analysis.put("proteinPercentage", (totalProtein / totalMacros) * 100);
            analysis.put("carbPercentage", (totalCarbs / totalMacros) * 100);
            analysis.put("fatPercentage", (totalFat / totalMacros) * 100);
        }

        Map<String, Double> caloriesByMealType = meals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMealType() != null ? m.getMealType().toString() : "OUTROS",
                        Collectors.summingDouble(MealModel::getTotalCalories)
                ));
        analysis.put("caloriesByMealType", caloriesByMealType);

        Map<LocalDate, Double> caloriesByDay = meals.stream()
                .collect(Collectors.groupingBy(
                        MealModel::getDate,
                        Collectors.summingDouble(MealModel::getTotalCalories)
                ));
        if (!caloriesByDay.isEmpty()) {
            Map.Entry<LocalDate, Double> maxDay = caloriesByDay.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            Map.Entry<LocalDate, Double> minDay = caloriesByDay.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElse(null);

            analysis.put("highestCalorieDay", Map.of("date", maxDay.getKey(), "calories", maxDay.getValue()));
            analysis.put("lowestCalorieDay", Map.of("date", minDay.getKey(), "calories", minDay.getValue()));
        }
        return analysis;
    }

    private Map<String, Object> getTrendsAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> trends = new HashMap<>();

        List<WeightHistoryModel> weights = weightHistoryRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        if (weights.size() >= 2) {
            weights.sort(Comparator.comparing(WeightHistoryModel::getDate));
            double firstWeight = weights.get(0).getWeight();
            double lastWeight = weights.get(weights.size() - 1).getWeight();
            double weightChange = lastWeight - firstWeight;
            double weightChangePercentage = (weightChange / firstWeight) * 100;

            trends.put("weightTrend", Map.of(
                    "initial", firstWeight,
                    "current", lastWeight,
                    "change", weightChange,
                    "changePercentage", weightChangePercentage,
                    "direction", weightChange > 0 ? "AUMENTANDO" : weightChange < 0 ? "DIMINUINDO" : "ESTÁVEL"
            ));
        }

        List<MealModel> meals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        Map<LocalDate, Double> dailyCalories = meals.stream()
                .collect(Collectors.groupingBy(
                        MealModel::getDate,
                        Collectors.summingDouble(MealModel::getTotalCalories)
                ));

        if (dailyCalories.size() >= 2) {
            List<Double> caloriesList = new ArrayList<>(dailyCalories.values());
            double avgFirstHalf = caloriesList.subList(0, caloriesList.size() / 2).stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0);
            double avgSecondHalf = caloriesList.subList(caloriesList.size() / 2, caloriesList.size()).stream()
                    .mapToDouble(Double::doubleValue).average().orElse(0);

            trends.put("calorieTrend", Map.of(
                    "firstHalfAverage", avgFirstHalf,
                    "secondHalfAverage", avgSecondHalf,
                    "trend", avgSecondHalf > avgFirstHalf ? "AUMENTANDO" : avgSecondHalf < avgFirstHalf ? "DIMINUINDO" : "ESTÁVEL"
            ));
        }

        List<WaterIntakeTrackerModel> waterIntakes = waterIntakeTrackerRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        if (!waterIntakes.isEmpty()) {
            double avgWater = waterIntakes.stream()
                    .mapToDouble(WaterIntakeTrackerModel::getAmountMl)
                    .average()
                    .orElse(0);
            long daysMetGoal = waterIntakes.stream()
                    .filter(w -> w.getAmountMl() >= w.getGoalMl())
                    .count();
            double goalCompletionRate = (daysMetGoal * 100.0) / waterIntakes.size();

            trends.put("hydrationTrend", Map.of(
                    "averageIntake", avgWater,
                    "goalCompletionRate", goalCompletionRate,
                    "daysMetGoal", daysMetGoal,
                    "totalDays", waterIntakes.size()
            ));
        }
        return trends;
    }

    private Map<String, Object> getPatternAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> patterns = new HashMap<>();
        List<MealModel> meals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        Map<DayOfWeek, Double> caloriesDayOfWeek = meals.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getDate().getDayOfWeek(),
                        Collectors.summingDouble(MealModel::getTotalCalories)
                ));

        Map<String, Object> formattedDayPatterns = new LinkedHashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            formattedDayPatterns.put(day.toString(), caloriesDayOfWeek.getOrDefault(day, 0.0));
        }
        patterns.put("caloriesByDayOfWeek", formattedDayPatterns);

        if (!caloriesDayOfWeek.isEmpty()) {
            Map.Entry<DayOfWeek, Double> maxDay = caloriesDayOfWeek.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            patterns.put("highestConsumptionDay", maxDay.getKey().toString());
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysWithMeals = meals.stream()
                .map(MealModel::getDate)
                .distinct()
                .count();
        double consistencyRate = (daysWithMeals * 100.0) / totalDays;

        patterns.put("consistency", Map.of(
                "daysWithMeals", daysWithMeals,
                "totalDays", totalDays,
                "consistencyRate", consistencyRate
        ));
        return patterns;
    }

    private Map<String, Object> getProgressAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> progress = new HashMap<>();

        List<WeightHistoryModel> weights = weightHistoryRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        if (!weights.isEmpty()) {
            weights.sort(Comparator.comparing(WeightHistoryModel::getDate));

            WeightHistoryModel first = weights.get(0);
            WeightHistoryModel last = weights.get(weights.size() - 1);

            long days = ChronoUnit.DAYS.between(first.getDate(), last.getDate());
            double weightChange = last.getWeight() - first.getWeight();
            double avgWeeklyChange = days > 0 ? (weightChange / days) * 7 : 0;

            progress.put("weightProgress", Map.of(
                    "startWeight", first.getWeight(),
                    "currentWeight", last.getWeight(),
                    "totalChange", weightChange,
                    "averageWeeklyChange", avgWeeklyChange,
                    "daysTracked", weights.size()
            ));
        }

        List<WaterIntakeTrackerModel> waterIntakes = waterIntakeTrackerRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        if (!waterIntakes.isEmpty()) {
            long totalDaysMetGoal = waterIntakes.stream()
                    .filter(w -> w.getAmountMl() >= w.getGoalMl())
                    .count();

            progress.put("hydrationProgress", Map.of(
                    "daysMetGoal", totalDaysMetGoal,
                    "totalDaysTracked", waterIntakes.size(),
                    "successRate", (totalDaysMetGoal * 100.0) / waterIntakes.size()
            ));
        }
        return progress;
    }

    private Map<String, Object> getComparativeAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> comparative = new HashMap<>();

        long periodDays = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate previousStart = startDate.minusDays(periodDays + 1);
        LocalDate previousEnd = startDate = startDate.minusDays(1);

        List<MealModel> currentMeals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        List<MealModel> previousMeals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        double currentAvgCalories = currentMeals.stream()
                .mapToDouble(MealModel::getTotalCalories)
                .average()
                .orElse(0);

        double previousAvgCalories = previousMeals.stream()
                .mapToDouble(MealModel::getTotalCalories)
                .average()
                .orElse(0);

        double caloriesChange = currentAvgCalories - previousAvgCalories;
        double caloriesChangePercent = previousAvgCalories > 0 ? (caloriesChange / previousAvgCalories) * 100 : 0;

        comparative.put("caloriesComparison", Map.of(
                "currentPeriodAvg", currentAvgCalories,
                "previousPeriodAvg", previousAvgCalories,
                "change", caloriesChange,
                "changePercentage", caloriesChangePercent
        ));
        return comparative;
    }

    private List<Map<String, Object>> generateRecommendationsAnalysis(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        List<MealModel> meals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        List<WaterIntakeTrackerModel> waterIntakes = waterIntakeTrackerRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);

        double avgCalories = meals.stream()
                .mapToDouble(MealModel::getTotalCalories)
                .average()
                .orElse(0);
        if (avgCalories < 1200) {
            recommendations.add(Map.of(
                    "type", "NUTRIÇÃO",
                    "priority", "ALTA",
                    "message", "Seu consumo está muito baixo. Considere aumentar para pelo menos 1200 kcal/dia."
            ));
        } else if (avgCalories > 3000) {
            recommendations.add(Map.of(
                    "type", "NUTRIÇÃO",
                    "priority", "ALTA",
                    "message", "Seu consumo calórico está elevado. Considere reduzir para aproximadamente 2000kcal/dia."
            ));
        }

        long daysMetWaterGoal = waterIntakes.stream()
                .filter(w -> w.getAmountMl() >= w.getGoalMl())
                .count();

        if (waterIntakes.size() > 0 && (daysMetWaterGoal * 100.0 / waterIntakes.size()) < 50) {
            recommendations.add(Map.of(
                    "type", "HIDRATAÇÃO",
                    "priority", "MÉDIA",
                    "message", "Você está atingindo sua meta de água em menos de 50% dos dias. Tente beber mais água!"
            ));
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long daysWithMeals = meals.stream()
                .map(MealModel::getDate)
                .distinct()
                .count();

        if (totalDays > 0 && (daysWithMeals * 100.0 / totalDays) < 70) {
            recommendations.add(Map.of(
                    "type", "CONSISTÊNCIA",
                    "priority", "MÉDIA",
                    "message", "Registre suas refeições todos os dia para melhor acompanhamento do seu progresso."
            ));
        }

        double avgProtein = meals.stream()
                .mapToDouble(MealModel::getTotalProtein)
                .average()
                .orElse(0);

        if (avgProtein < 50) {
            recommendations.add(Map.of(
                    "type", "MACRONUTRIENTES",
                    "priority", "MÉDIA",
                    "message", "Considere aumentar o consumo de proteinas para pelo menos 50g por dia."
            ));
        }
        return recommendations;
    }

    private Map<String, Object> calculateHealthScore(Long userId, LocalDate starDate, LocalDate endDte) {
        Map<String, Object> score = new HashMap<>();
        double totalScore = 0;
        int factors = 0;

        List<MealModel> meals = mealRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, starDate, endDte);
        List<WaterIntakeTrackerModel> waterIntakes = waterIntakeTrackerRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, starDate, endDte);

        long totalDays = ChronoUnit.DAYS.between(starDate, endDte) + 1;
        long daysWithMeals = meals.stream()
                .map(MealModel::getDate)
                .distinct()
                .count();
        double consistencyScore = (daysWithMeals * 30.0) / totalDays;

        totalScore += consistencyScore;
        factors++;

        double avgCalories = meals.stream()
                .mapToDouble(MealModel::getTotalCalories)
                .average()
                .orElse(0);
        double nutritionScore = 0;
        if (avgCalories >= 1500 && avgCalories <= 2500) {
            nutritionScore = 30;
        } else if (avgCalories >= 1200 && avgCalories <= 3000) {
            nutritionScore = 20;
        } else {
            nutritionScore = 10;
        }
        totalScore += nutritionScore;
        factors++;

        double hydrationScore = 0;
        if (!waterIntakes.isEmpty()) {
            long daysMetGoal = waterIntakes.stream()
                    .filter(w -> w.getAmountMl() >= w.getGoalMl())
                    .count();
            hydrationScore = (daysMetGoal * 20.0) / waterIntakes.size();
        }
        totalScore += hydrationScore;
        factors++;

        Map<LocalDate, Double> dailyCalories = meals.stream()
                .collect(Collectors.groupingBy(
                        MealModel::getDate,
                        Collectors.summingDouble(MealModel::getTotalCalories)
                ));

        double variationScore = 20;
        if (dailyCalories.size() >= 2) {
            double avg = dailyCalories.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);
            double variance = dailyCalories.values().stream()
                    .mapToDouble(c -> Math.pow(c - avg, 2))
                    .average()
                    .orElse(0);
            double stdDev = Math.sqrt(variance);

            if (stdDev > 500) {
                variationScore = 10;
            } else if (stdDev > 300) {
                variationScore = 15;
            }
        }
        totalScore += variationScore;
        factors++;

        double finalScore = factors > 0 ? totalScore / factors * 25 : 0;

        score.put("score", Math.round(finalScore * 10) / 10.0);
        score.put("consistencyScore", Math.round(consistencyScore * 10) / 10.0);
        score.put("nutritionScore", Math.round(nutritionScore * 10) / 10.0);
        score.put("hydrationScore", Math.round(hydrationScore * 10) / 10.0);
        score.put("variationScore", Math.round(variationScore * 10) / 10.0);

        String grade;
        if (finalScore >= 90) grade = "EXCELENTE";
        else if (finalScore >= 75) grade = "BOM";
        else if (finalScore >= 60) grade = "REGULAR";
        else grade = "PRECISA MELHORAR";

        score.put("grade", grade);

        return score;
    }
}