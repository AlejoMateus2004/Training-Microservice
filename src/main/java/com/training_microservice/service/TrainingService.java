package com.training_microservice.service;

import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.domain.entities.Training;
import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.mapper.TrainingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TrainingService {

    private TrainingMapper trainingMapper;
    private TrainingRepo trainingRepository;

    @Transactional
    public ResponseEntity saveTraining(TrainingRecord.TrainingRequest trainingRequest) {
        try {
            Training training = trainingMapper.trainingRequestToTraining(trainingRequest);
            Training savedTraining =trainingRepository.save(training);
            if (savedTraining != null) {
                log.info("Training created: {}", savedTraining.getTrainingName());
                return ResponseEntity.ok().build();
            } else {
                log.error("Failed to save training. Null response from repository.");
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error, saving Training", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    public ResponseEntity updateTrainingStatusToCompleted(Long trainingId) {
        try {
            Training training = trainingRepository.findById(trainingId).orElse(null);
            if (training == null) {
                throw new IllegalArgumentException("Training not found with ID: " + trainingId);
            }
            if (training.getTrainingIsCompleted()) {
                return ResponseEntity.ok().build();
            }
            training.setTrainingIsCompleted(true);
            Training updatedTraining = trainingRepository.save(training);
            if (updatedTraining != null) {
                log.info("Training status updated: {}", updatedTraining.getTrainingName());
                return ResponseEntity.ok().build();
            } else {
                log.error("Failed to update training status");
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error, updating Training Status", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<TrainingRecord.TrainerTrainingSummary> getTrainingSummaryByTrainer(String trainerUsername) {
        try {
            // Retrieve trainings for the given trainer username
            List<Training> trainings = trainingRepository.findTrainingByTrainer(trainerUsername);

            if (trainings == null || trainings.isEmpty()) {
                return ResponseEntity.notFound().build(); // Return 404 if no trainings found
            }

            // Create a map to store the summary for each year and month
            Map<Integer, Map<String, Long>> trainerSummaryMap = new HashMap<>();

            // Iterate over each training
            for (Training training : trainings) {
                if (!training.getTrainingIsCompleted()) {
                   continue; 
                }
                // Get year, month, and duration from the training
                int year = training.getTrainingDate().getYear();
                String month = Month.of(training.getTrainingDate().getMonthValue()).toString();
                long duration = training.getTrainingDuration();

                // If the year is not present in the trainer's map, create a new map for the year
                trainerSummaryMap.putIfAbsent(year, new HashMap<>());

                // Get the map for the year
                Map<String, Long> monthDurationMap = trainerSummaryMap.get(year);

                // Update the total duration for the month
                monthDurationMap.merge(month, duration, Long::sum);
            }

            // Create a TrainerTrainingSummary object
            TrainingRecord.TrainerTrainingSummary trainerTrainingSummary =
                    new TrainingRecord.TrainerTrainingSummary(trainerSummaryMap);

            return ResponseEntity.ok().body(trainerTrainingSummary); // Return response if trainings exist
        } catch (Exception e) {
            // Log and return 500 Internal Server Error if an exception occurs
            log.error("Error occurred while retrieving trainings for trainer: {}", trainerUsername, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteTrainingById(Long IdTraining) {
        try {
            Training training =  trainingRepository.findById(IdTraining).orElse(null);
            if (training == null) {
                throw new IllegalArgumentException("Training not found with ID: " + IdTraining);
            }
            boolean isCompleted = training.getTrainingIsCompleted();
            if (!isCompleted) {
                trainingRepository.deleteTrainingById(IdTraining);
                log.info("Training deleted: {}", IdTraining);
                return ResponseEntity.ok().build();
            }
            log.info("Training is completed, can't be deleted: {}", IdTraining);
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            log.error("Error occurred while deleting training: {}", IdTraining, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<TrainingRecord.TrainerTrainingResponse>> getTrainerTrainingListByTrainingParams(TrainingRecord.TrainingParamsRequest trainingParams){
        String trainerUsername = trainingParams.trainerUsername();
        LocalDate periodFrom = trainingParams.periodFrom();
        LocalDate periodTo = trainingParams.periodTo();
        String traineeUsername = trainingParams.traineeUsername();

        List<Training> trainings = trainingRepository.findTrainingByTrainerUsernameAndTrainingParams(trainerUsername, periodFrom, periodTo, traineeUsername);
        if (trainings == null || trainings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<TrainingRecord.TrainerTrainingResponse> list = new ArrayList<>();
        for (Training training : trainings) {
            list.add(trainingMapper.trainingToTrainerTrainingResponse(training));
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<TrainingRecord.TraineeTrainingResponse>> getTraineeTrainingListByTrainingParams(TrainingRecord.TrainingParamsRequest trainingParams){
        String traineeUsername = trainingParams.traineeUsername();
        String trainerUsername = trainingParams.trainerUsername();
        LocalDate periodFrom = trainingParams.periodFrom();
        LocalDate periodTo = trainingParams.periodTo();

        List<Training> trainings = trainingRepository.findTrainingByTraineeUsernameAndTrainingParams(traineeUsername, periodFrom, periodTo, trainerUsername);
        if (trainings == null || trainings.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<TrainingRecord.TraineeTrainingResponse> list = new ArrayList<>();
        for (Training training : trainings) {
            list.add(trainingMapper.trainingToTraineeTrainingResponse(training));
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
