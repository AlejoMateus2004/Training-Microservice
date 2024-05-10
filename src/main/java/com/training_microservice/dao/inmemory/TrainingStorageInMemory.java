package com.training_microservice.dao.inmemory;

import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.domain.entities.Training;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TrainingStorageInMemory implements TrainingRepo {

    private static final Map<Long, Training> trainingMap = new HashMap<>();

    @Override
    public Optional<Training> findById(Long Id) {
        return Optional.ofNullable(trainingMap.get(Id));
    }

    @Override
    public Training save(Training value) {
        if (value == null) {
            return null;
        }
        trainingMap.put(value.getId(), value);
        return trainingMap.get(value.getId());
    }


    @Override
    public List<Training> findTrainingByTrainer(String username) {
        return trainingMap.values().stream()
                .filter(t -> t.getTrainerUsername().equals(username))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTrainingById(Long id) {
        trainingMap.remove(id);
    }

    @Override
    public List<Training> findTrainingByTrainerUsernameAndTrainingParams(String trainerUsername, LocalDate periodFrom, LocalDate periodTo, String traineeUsername) {
        return List.of();
    }

    @Override
    public List<Training> findTrainingByTraineeUsernameAndTrainingParams(String traineeUsername, LocalDate periodFrom, LocalDate periodTo, String trainerUsername) {
        return List.of();
    }


}
