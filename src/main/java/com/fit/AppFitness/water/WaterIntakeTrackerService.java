package com.fit.AppFitness.water;

import com.fit.AppFitness.dto.WaterIntakeTrackerDTO;
import com.fit.AppFitness.user.UserModel;
import com.fit.AppFitness.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WaterIntakeTrackerService {

    private final WaterIntakeTrackerRepository waterIntakeTrackerRepository;
    private final UserRepository userRepository;

    @Transactional
    public WaterIntakeTrackerModel addWaterIntake(Long userId, WaterIntakeTrackerDTO dto){
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        WaterIntakeTrackerModel waterIntake = waterIntakeTrackerRepository
                .findByUserIdAndDate(userId, dto.getDate())
                .orElse(new WaterIntakeTrackerModel());

        if(waterIntake.getId() == null){
            waterIntake.setUser(user);
            waterIntake.setDate(dto.getDate());
            waterIntake.setAmountMl(dto.getAmountMl());
            waterIntake.setGoalMl(dto.getGoalMl() != null ? dto.getGoalMl() : 2000.0);
        }else{
            waterIntake.setAmountMl(waterIntake.getAmountMl() + dto.getAmountMl());
        }

        waterIntake.setNotes(dto.getNotes());

        return waterIntakeTrackerRepository.save(waterIntake);
    }

    public WaterIntakeTrackerModel getTodayIntake(Long userId){
        return waterIntakeTrackerRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElse(null);
    }

    public List<WaterIntakeTrackerModel> getWaterIntakeHistory(Long userId, LocalDate startDate, LocalDate endDate){
        if(startDate != null && endDate != null){
            return waterIntakeTrackerRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                    userId, startDate, endDate);
        }
        return waterIntakeTrackerRepository.findByUserIdOrderByDateDesc(userId);
    }

    public Map<String, Object> getStats(Long userId){
        Map<String, Object> stats = new HashMap<>();

        WaterIntakeTrackerModel today = getTodayIntake(userId);
        if(today != null){
            stats.put("Quantidade do dia", today.getAmountMl());
            stats.put("Meta do dia", today.getGoalMl());
            stats.put("Progresso do dia", today.getProgressPercentage());
            stats.put("Meta Alcançada", today.getAmountMl() >= today.getGoalMl());
        }else{
            stats.put("Quantidade do dia", 0.0);
            stats.put("Meta do dia", 2000.0);
            stats.put("Progresso do dia", 0.0);
            stats.put("Meta Alcançada", false);
        }

        Double average = waterIntakeTrackerRepository.getAverageIntake(userId);
        stats.put("Média de consumo", average != null ? average : 0.0);

        Long goalsReached = waterIntakeTrackerRepository.countGoalsReached(userId);
        stats.put("Dias que bateu a meta", goalsReached);

        long totalRecords = waterIntakeTrackerRepository.countByUserId(userId);
        stats.put("Total de registros", totalRecords);

        if(totalRecords > 0){
            double successRate = (goalsReached.doubleValue() / totalRecords) * 100;
            stats.put("Taxa de sucesso", successRate);
        }else{
            stats.put("Taxa de sucesso", 0.0);
        }

        return stats;
    }

    @Transactional
    public WaterIntakeTrackerModel updateWaterIntake(Long userId, Long id, WaterIntakeTrackerDTO dto){
        WaterIntakeTrackerModel waterIntake = waterIntakeTrackerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de consumo de água não encontrado"));

        if(!waterIntake.getUser().getId().equals(userId)){
            throw new RuntimeException("Não autorizado");
        }

        waterIntake.setAmountMl(dto.getAmountMl());
        waterIntake.setDate(dto.getDate());
        if(dto.getGoalMl() != null){
            waterIntake.setGoalMl(dto.getGoalMl());
        }
        waterIntake.setNotes(dto.getNotes());

        return waterIntakeTrackerRepository.save(waterIntake);
    }

    @Transactional
    public void deleteWaterIntake(Long userId, Long id){
        WaterIntakeTrackerModel waterIntake = waterIntakeTrackerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro do consumo de água não encontrado"));

        if (!waterIntake.getUser().getId().equals(userId)) {
            throw new RuntimeException("Não autorizado");
        }

        waterIntakeTrackerRepository.delete(waterIntake);
    }
}
