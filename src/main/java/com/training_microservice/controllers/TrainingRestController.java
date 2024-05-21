package com.training_microservice.controllers;

import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Training Controller", description = "Operations for creating Trainings in the application")
@RestController
@RequestMapping("/training")
public class TrainingRestController {

    @Autowired
    private TrainingService trainingService;

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation( summary= "Save Training")
    @PostMapping
    public ResponseEntity saveTraining(@RequestBody @Validated TrainingRecord.TrainingRequest trainingRequest){
        return trainingService.saveTraining(trainingRequest);
    }

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation( summary= "Update Training Status")
    @PutMapping("/status/{trainingId}")
    public ResponseEntity updateTrainingStatusToCompleted(@PathVariable Long trainingId){
        return trainingService.updateTrainingStatusToCompleted(trainingId);
    }

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation(summary = "Get Training Summary by Trainer username")
    @GetMapping("/summary/trainer/{trainerUsername}")
    public ResponseEntity<TrainingRecord.TrainerTrainingSummary> getTrainingSummaryByTrainerUsername(@PathVariable String trainerUsername){
        return trainingService.getTrainingSummaryByTrainer(trainerUsername);
    }

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation(summary = "Get Training List by Trainer username, and Training Params")
    @PostMapping("/list/trainer")
    public ResponseEntity<List<TrainingRecord.TrainerTrainingResponse>> getTrainerTrainingListByTrainingParams(@RequestBody TrainingRecord.TrainingParamsRequest trainingParams){
        return trainingService.getTrainerTrainingListByTrainingParams(trainingParams);
    }

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation(summary = "Get Training List by Trainee username, and Training Params")
    @PostMapping("/list/trainee")
    public ResponseEntity<List<TrainingRecord.TraineeTrainingResponse>> getTraineeTrainingListByTrainingParams(@RequestBody TrainingRecord.TrainingParamsRequest trainingParams){
        return trainingService.getTraineeTrainingListByTrainingParams(trainingParams);
    }

    @Parameter(name = "Authorization", description = "Authorization Token Bearer", required = true,
            schema = @Schema(type = "string"), example = "Bearer")
    @Operation(summary = "Delete Training By ID")
    @DeleteMapping("/{IdTraining}")
    public ResponseEntity<Void> deleteTrainingById(@PathVariable Long IdTraining){
        return trainingService.deleteTrainingById(IdTraining);
    }

}
