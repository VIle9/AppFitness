package com.fit.AppFitness.goal;

import com.fit.AppFitness.dto.CustomGoalDTO;
import com.fit.AppFitness.goal.enums.GoalStatus;
import com.fit.AppFitness.goal.enums.GoalType;
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
public class CustomGoalService {

    private final CustomGoalRepository customGoalRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomGoalModel createdGoal(Long userId, CustomGoalDTO dto){
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        CustomGoalModel goal = new CustomGoalModel();
        goal.setUser(user);
        goal.setName(dto.getName());
        goal.setDescription(dto.getDescription());
        goal.setTargetValue(dto.getTargetValue());
        goal.setCurrentValue(dto.getCurrentValue() != null ? dto.getCurrentValue() : 0.0);
        goal.setUnits(dto.getUnits());
        goal.setNotifyUser(dto.getNotifyUser() != null ? dto.getNotifyUser() : true);
        goal.setActive(dto.getActive() != null ? dto.getActive() : true);
        goal.setFrequency(dto.getFrequency());
        goal.setType(dto.getType());
        goal.setStatus(dto.getStatus() != null ? dto.getStatus() : GoalStatus.NOT_STARTED);
        goal.setStartDate(dto.getStartDate());
        goal.setEndDate(dto.getEndDate());

        if(goal.getStartDate().isBefore(LocalDate.now()) || goal.getStartDate().isEqual(LocalDate.now())){
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        return customGoalRepository.save(goal);
    }

    public List<CustomGoalModel> getAllGoals(Long userId){
        return customGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<CustomGoalModel> getActiveGoals(Long userId){
        return customGoalRepository.findByUserIdAndActiveOrderByCreatedAtDesc(userId, true);
    }

    public List<CustomGoalModel> getGoalsByStatus(Long userId, GoalStatus status){
        return customGoalRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }

    public List<CustomGoalModel> getGoalsByType(Long userId, GoalType type){
        return customGoalRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }

    public CustomGoalModel getGoalById(Long userId, Long goalId){
        CustomGoalModel goal = customGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada."));

        if(!goal.getUser().getId().equals(userId)){
            throw new RuntimeException("Não autorizado.");
        }

        return goal;
    }

    @Transactional
    public CustomGoalModel updateGoal(Long userId, Long goalId, CustomGoalDTO dto){
        CustomGoalModel goal = getGoalById(userId, goalId);

        goal.setName(dto.getName());
        goal.setDescription(dto.getDescription());
        goal.setTargetValue(dto.getTargetValue());
        goal.setCurrentValue(dto.getCurrentValue() != null ? dto.getCurrentValue() : goal.getCurrentValue());
        goal.setUnits(dto.getUnits());
        goal.setNotifyUser(dto.getNotifyUser());
        dto.setActive(dto.getActive());
        goal.setFrequency(dto.getFrequency());
        goal.setType(dto.getType());
        goal.setStatus(dto.getStatus());
        goal.setStartDate(dto.getStartDate());
        goal.setEndDate(dto.getEndDate());

        return customGoalRepository.save(goal);
    }

    @Transactional
    public CustomGoalModel updateProgress(Long userId, Long goalId, Double newValue){
        CustomGoalModel goal = getGoalById(userId, goalId);

        goal.setCurrentValue(newValue);

        if(newValue >= goal.getTargetValue()){
            goal.setStatus(GoalStatus.COMPLETED);
        }else if(goal.getStatus() == GoalStatus.NOT_STARTED){
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        return customGoalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId){
        CustomGoalModel goal = getGoalById(userId, goalId);
        customGoalRepository.delete(goal);
    }

    @Transactional
    public CustomGoalModel toggleActive(Long userId, Long goalId){
        CustomGoalModel goal = getGoalById(userId, goalId);
        goal.setActive(!goal.getActive());
        return customGoalRepository.save(goal);
    }

    public Map<String, Object> getGoalStatus(Long userId){
        Map<String, Object> stats = new HashMap<>();

        Long total = (long) customGoalRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        Long completed = customGoalRepository.countByUserIdAndStatus(userId, GoalStatus.COMPLETED);
        Long inProgress = customGoalRepository.countByUserIdAndStatus(userId, GoalStatus.IN_PROGRESS);
        Long failed = customGoalRepository.countByUserIdAndStatus(userId, GoalStatus.FAILED);

        stats.put("Total de Metas.", total);
        stats.put("Completadas", completed);
        stats.put("Em progresso.", inProgress);
        stats.put("Falhou.", failed);
        stats.put("Taxa de sucesso.", total > 0 ? (completed.doubleValue() / total) * 100 : 0.0);

        return stats;
    }

    public void checkAndUpdateExpiredGoals(Long userId){
        List<CustomGoalModel> expiredGOals = customGoalRepository.findExpiredGoals(userId, LocalDate.now());

        for(CustomGoalModel goal : expiredGOals){
            if(goal.getCurrentValue() >= goal.getTargetValue()){
                goal.setStatus(GoalStatus.COMPLETED);
            }else{
                goal.setStatus(GoalStatus.FAILED);
            }

            customGoalRepository.save(goal);
        }
    }
}
