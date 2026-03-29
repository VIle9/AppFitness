package com.fit.AppFitness.weight;

import com.fit.AppFitness.dto.WeightHistoryDTO;
import com.fit.AppFitness.user.UserModel;
import com.fit.AppFitness.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeightHistoryService {

    private final WeightHistoryRepository weightHistoryRepository;
    private final UserRepository userRepository;

    public WeightHistoryService(WeightHistoryRepository weightHistoryRepository, UserRepository userRepository) {
        this.weightHistoryRepository = weightHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public WeightHistoryDTO.WeightResponse addWeight(Long userId, WeightHistoryDTO.CreateWeightRequest request){
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        weightHistoryRepository.findByUserIdAndDate(userId, request.getDate())
                .ifPresent(existing -> {
                    throw new RuntimeException("Já existe um registro de peso para esta data.");
                });

        WeightHistoryModel weightHistory = new WeightHistoryModel();
        weightHistory.setUser(user);
        weightHistory.setWeight(request.getWeight());
        weightHistory.setDate(request.getDate());
        weightHistory.setNotes(request.getNotes());

        WeightHistoryModel saved = weightHistoryRepository.save(weightHistory);

        updateUserWeightIfNeeded(user, request.getDate(), request.getWeight());

        return mapToResponse(saved);
    }

    @Transactional
    public WeightHistoryDTO.WeightResponse updateWeight(Long userId, Long weightId, WeightHistoryDTO.UpdateWeightRequest request){
        WeightHistoryModel weightHistory = weightHistoryRepository.findById(weightId)
                .orElseThrow(() -> new RuntimeException("Registro de peso não encontrado."));

        if(!weightHistory.getUser().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado.");
        }

        weightHistory.setWeight(request.getWeight());
        weightHistory.setNotes(request.getNotes());

        WeightHistoryModel updated = weightHistoryRepository.save(weightHistory);

        updateUserWeightIfNeeded(weightHistory.getUser(), weightHistory.getDate(), request.getWeight());

        return mapToResponse(updated);
    }

    public List<WeightHistoryDTO.WeightResponse> getHistory(Long userId){
        List<WeightHistoryModel> history = weightHistoryRepository.findByUserIdOrderByDateDesc(userId);
        return history.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<WeightHistoryDTO.WeightResponse> getHistoryByPeriod(Long userId, LocalDate startDate, LocalDate endDate){
        List<WeightHistoryModel> history = weightHistoryRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return history.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public WeightHistoryDTO.WeightStatsResponse getStats(Long userId){
        List<WeightHistoryModel> history = weightHistoryRepository.findByUserIdOrderByDateDesc(userId);

        if(history.isEmpty()){
            return new WeightHistoryDTO.WeightStatsResponse(null, null, null, null, 0, null);
        }

        WeightHistoryModel latest = history.get(0);
        WeightHistoryModel oldest = history.get(history.size() - 1);

        Double currentWeight = latest.getWeight();
        Double startWeight = oldest.getWeight();
        Double weightChange = currentWeight - startWeight;

        Double averageWeight = history.stream()
                .mapToDouble(WeightHistoryModel::getWeight)
                .average()
                .orElse(0.0);

        return new WeightHistoryDTO.WeightStatsResponse(
                currentWeight,
                startWeight,
                weightChange,
                Math.round(averageWeight * 10.0) / 10.0,
                history.size(),
                latest.getDate()
        );
    }

    @Transactional
    public void deleteWeight(Long userId, Long weightId){
        WeightHistoryModel weightHistory = weightHistoryRepository.findById(weightId)
                .orElseThrow(() -> new RuntimeException("Registro de peso não encontrado."));

        if(!weightHistory.getUser().getId().equals(userId)){
            throw new RuntimeException("Acesso negado.");
        }

        weightHistoryRepository.delete(weightHistory);

        weightHistoryRepository.findFirstByUserIdOrderByDateDesc(userId)
                .ifPresent(latest -> {
                    UserModel user = userRepository.findById(userId).orElseThrow();
                    user.setWeight(latest.getWeight());
                    userRepository.save(user);
                });
    }

    private void updateUserWeightIfNeeded(UserModel user, LocalDate date, Double weight){
        weightHistoryRepository.findFirstByUserIdOrderByDateDesc(user.getId())
                .ifPresent(latestWeight -> {
                    if(date.equals(latestWeight.getDate()) || date.isAfter(latestWeight.getDate())){
                        user.setWeight(weight);
                        userRepository.save(user);
                    }
                });
    }

    private WeightHistoryDTO.WeightResponse mapToResponse(WeightHistoryModel model){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return new WeightHistoryDTO.WeightResponse(
                model.getId(),
                model.getWeight(),
                model.getDate(),
                model.getNotes(),
                model.getCreatedAt().format(formatter)
        );
    }
}

