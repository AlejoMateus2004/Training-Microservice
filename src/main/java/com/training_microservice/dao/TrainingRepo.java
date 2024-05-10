package com.training_microservice.dao;

import com.training_microservice.domain.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepo {
    Training save(Training value);
    List<Training> findTrainingByTrainee(String username);
    List<Training> findTrainingByTrainer(String username);
    void deleteTrainingByTrainerUsername(String trainerUsername);
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
