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
    public ResponseEntity<String> deleteTrainingByTrainerUsername(String trainerUsername) {
        try {
            trainingRepository.deleteTrainingByTrainerUsername(trainerUsername);
            return ResponseEntity.ok().body("Training Deleted");
        }catch (Exception e){
            log.error("Error occurred while deleting training: {}", trainerUsername, e);
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

}
