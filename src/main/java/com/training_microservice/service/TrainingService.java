package com.training_microservice.service;

import com.training_microservice.dao.TrainerSummaryRepo;
import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.domain.documents.TrainerSummary;
import com.training_microservice.domain.entities.Training;
import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.mapper.TrainingMapper;
import jakarta.validation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class TrainingService {

    private TrainingMapper trainingMapper;
    private TrainingRepo trainingRepository;
    private TrainerSummaryRepo trainerSummaryRepo;

    @Transactional
    public ResponseEntity saveTraining(TrainingRecord.TrainingRequest trainingRequest) {
        try {
            Set<ConstraintViolation<TrainingRecord.TrainingRequest>> violations;

            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                Validator validator = factory.getValidator();
                violations = validator.validate(trainingRequest);
            }

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            Training training = trainingMapper.trainingRequestToTraining(trainingRequest);
            if (training == null) {
                return ResponseEntity.badRequest().build();

            }
            Training savedTraining = trainingRepository.save(training);
            TrainerSummary trainerSummary = trainingRequestToTrainerSummary(trainingRequest);

            if (savedTraining != null) {
                trainerSummaryRepo.save(trainerSummary);
                log.info("Training created: {}", savedTraining.getTrainingName());
                return ResponseEntity.ok().build();
            } else {
                log.error("Failed to save training. Null response from repository.");
            }
            return ResponseEntity.badRequest().build();
        }catch (ConstraintViolationException cVex){
            log.error("Constraint Violation Exception");
            cVex.getConstraintViolations().forEach(e->log.error(e.getMessage()));
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
                return ResponseEntity.notFound().build();
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
    public ResponseEntity<TrainerSummary> getTrainingSummaryByTrainer(String trainerUsername) {
        try {
            // Retrieve trainings for the given trainer username
            TrainerSummary trainerSummary = trainerSummaryRepo.findById(trainerUsername).orElse(null);

            if (trainerSummary == null) {
                return ResponseEntity.notFound().build(); // Return 404 if no trainings found
            }

            return ResponseEntity.ok().body(trainerSummary); // Return response if trainings exist
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
                return ResponseEntity.notFound().build();
            }

            boolean isCompleted = training.getTrainingIsCompleted();
            if (!isCompleted) {
                TrainerSummary trainerSummary = trainerSummaryRepo.findById(training.getTrainerUsername()).orElse(null);
                if (trainerSummary == null) {
                    return ResponseEntity.notFound().build();
                }
                trainingRepository.deleteTrainingById(IdTraining);
                TrainerSummary trainerSummary1 = deleteTrainingDuration(trainerSummary, training.getTrainingDate(), training.getTrainingDuration());
                if (trainerSummary1 == null) {
                    trainerSummaryRepo.deleteById(training.getTrainerUsername());
                }else{
                    trainerSummaryRepo.save(trainerSummary1);
                }
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
        LocalDate periodFrom = trainingParams.periodFrom();
        LocalDate periodTo = trainingParams.periodTo();
        String trainerUsername = trainingParams.trainerUsername();

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

    private TrainerSummary trainingRequestToTrainerSummary(TrainingRecord.TrainingRequest trainingRequest) {

        String trainerUsername = trainingRequest.trainerUsername();
        TrainerSummary trainerSummary = trainerSummaryRepo.findById(trainerUsername)
                .orElseGet(() -> createTrainerSummaryFromRequest(trainingRequest));

        Map<Integer, Map<String, Long>> trainerSummaryMap = trainerSummary.getSummary();

        // Check if the trainerSummaryMap is null and, if so, initialise it
        if (trainerSummaryMap == null) {
            trainerSummaryMap = new HashMap<>();
        }

        int year = trainingRequest.trainingDate().getYear();
        String month = Month.of(trainingRequest.trainingDate().getMonthValue()).toString();
        long duration = trainingRequest.trainingDuration();

        // If the year is not present in the trainer's map, create a new map for the year
        trainerSummaryMap.putIfAbsent(year, new HashMap<>());

        // Get the map for the year
        Map<String, Long> monthDurationMap = trainerSummaryMap.get(year);

        // Update the total duration for the month
        monthDurationMap.merge(month, duration, Long::sum);

        trainerSummary.setSummary(trainerSummaryMap);

        return trainerSummary;
    }

    private TrainerSummary createTrainerSummaryFromRequest(TrainingRecord.TrainingRequest trainingRequest) {
        TrainerSummary trainerSummary = new TrainerSummary();
        trainerSummary.setTrainerUsername(trainingRequest.trainerUsername());
        trainerSummary.setTrainerFirstName(trainingRequest.trainerFirstName());
        trainerSummary.setTrainerLastName(trainingRequest.trainerLastName());
        trainerSummary.setTrainerStatus(trainingRequest.trainerStatus());
        return trainerSummary;
    }

    private TrainerSummary deleteTrainingDuration(TrainerSummary trainerSummary, LocalDate date, long trainingDuration){
        Map<Integer, Map<String, Long>> trainerSummaryMap = trainerSummary.getSummary();

        // Check if the trainerSummaryMap is null and, if so, initialise it
        if (trainerSummaryMap == null) {
            return null;
        }

        int year = date.getYear();
        String month = Month.of(date.getMonthValue()).toString();

        // Get the map for the year
        Map<String, Long> monthDurationMap = trainerSummaryMap.get(year);
        if (monthDurationMap == null) {
            return null;
        }
        Long currentDuration = monthDurationMap.get(month);
        if (currentDuration != null) {
            // Decrease the total duration for the month by training duration
            long newDuration = currentDuration - trainingDuration;

            if (newDuration <= 0) {
                // If the new duration is 0 or negative, remove the entry from the map
                monthDurationMap.remove(month);
            } else {
                // Otherwise, update the total duration for the month
                monthDurationMap.put(month, newDuration);
            }
        }
        if (monthDurationMap.isEmpty()) {
            trainerSummaryMap.remove(year);
        }
        if (trainerSummaryMap.isEmpty()) {
            return null;
        }
        trainerSummary.setSummary(trainerSummaryMap);

        return trainerSummary;
    }

}
