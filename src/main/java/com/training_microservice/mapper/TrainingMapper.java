package com.training_microservice.mapper;

import com.training_microservice.domain.entities.Training;
import com.training_microservice.domain.records.TrainingRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    Training trainingRequestToTraining(TrainingRecord.TrainingRequest trainingRequest);
    TrainingRecord.TraineeTrainingResponse trainingToTraineeTrainingResponse(Training training);
    TrainingRecord.TrainerTrainingResponse trainingToTrainerTrainingResponse(Training training);

}
