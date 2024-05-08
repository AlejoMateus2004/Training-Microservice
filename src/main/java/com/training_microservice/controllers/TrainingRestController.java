package com.training_microservice.controllers;

import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Training Controller", value = "Operations for creating Trainings in the application")
@RestController
@RequestMapping("/training")
public class TrainingRestController {

    @Autowired
    private TrainingService trainingService;

//    @ApiImplicitParam(name = "Authorization", value = "Authorization Token Bearer", required = true,
//            dataTypeClass = String.class, paramType = "header", example = "Bearer")
    @ApiOperation(value = "Save Training", notes = "Register a new Training in the system")
    @PostMapping
    public ResponseEntity saveTraining(@RequestBody @Validated TrainingRecord.TrainingRequest trainingRequest){
        return trainingService.saveTraining(trainingRequest);
    }

    @ApiOperation(value = "Get Training List by Trainer username", notes = "Retrieve Training List by Trainer username")
//    @ApiImplicitParam(name = "Authorization", value = "Authorization Token Bearer", required = true,
//            dataTypeClass = String.class, paramType = "header", example = "Bearer")
    @GetMapping("/summary/trainer/{trainerUsername}")
    public ResponseEntity<TrainingRecord.TrainerTrainingSummary> getTrainingSummaryByTrainerUsername(@PathVariable String trainerUsername){
        return trainingService.getTrainingSummaryByTrainer(trainerUsername);
    }

    @ApiOperation(value = "Delete Trainings By Trainer Username")
//    @ApiImplicitParam(name = "Authorization", value = "Authorization Token Bearer", required = true,
//            dataTypeClass = String.class, paramType = "header", example = "Bearer")
    @DeleteMapping("/{trainerUsername}")
    public ResponseEntity<String> deleteTrainingByTrainerUsername(@PathVariable String trainerUsername){
        return trainingService.deleteTrainingByTrainerUsername(trainerUsername);
    }
}
