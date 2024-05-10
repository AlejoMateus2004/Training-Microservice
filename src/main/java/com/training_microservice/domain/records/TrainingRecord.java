package com.training_microservice.domain.records;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public class TrainingRecord {

    public record TrainingParamsRequest(
        LocalDate periodFrom,
        LocalDate periodTo,
        String trainerUsername,
        String traineeUsername
        ){
    }


    public record TraineeTrainingResponse(
            String trainingName,
            LocalDate trainingDate,
            String trainerUsername,
            Long trainingDuration
    ){
    }

    public record TrainerTrainingResponse(
            String trainingName,
            LocalDate trainingDate,
            String traineeUsername,
            Long trainingDuration
    ){
    }

    public record TrainingRequest(
            @NotBlank(message = "Trainee username can't be null or empty")
            String traineeUsername,
            @NotBlank(message = "Trainer username can't be null or empty")
            String trainerUsername,
            @NotBlank(message = "Training Name can't be null or empty")
            String trainingName,
            @NotNull(message = "Training Date can't be null or empty")
            LocalDate trainingDate,
            @NotNull(message = "Training Duration can't be null or empty")
            Long trainingDuration
            ){}

    public record TrainerTrainingSummary (
            Map<Integer, Map<String, Long>> summary
    ){
    }
}
