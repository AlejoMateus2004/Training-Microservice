package com.training_microservice.dao;

import com.training_microservice.domain.entities.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepo {
    Training save(Training value);
    Optional<Training> findById(Long value);

    List<Training> findAll();
    List<Training> findTrainingByTrainee(String username);
    List<Training> findTrainingByTrainer(String username);

    void deleteTrainingByTrainerUsername(String trainerUsername);
}
