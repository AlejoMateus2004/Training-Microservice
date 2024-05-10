package com.training_microservice.dao;

import com.training_microservice.domain.entities.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepo {
    Optional<Training> findById(Long Id);
    Training save(Training value);
    List<Training> findTrainingByTrainer(String username);
    void deleteTrainingById(Long Id);
    List<Training> findTrainingByTrainerUsernameAndTrainingParams(
            String trainerUsername,
            LocalDate periodFrom,
            LocalDate periodTo,
            String traineeUsername
    );
    List<Training> findTrainingByTraineeUsernameAndTrainingParams(
            String traineeUsername,
            LocalDate periodFrom,
            LocalDate periodTo,
            String trainerUsername
    );
}
