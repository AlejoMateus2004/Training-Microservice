package com.training_microservice.service.messaging;

import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.jms.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "Training Consumer", description = "Operations for creating Trainings in the application")
@Component
@AllArgsConstructor
public class TrainingConsumer {

    private TrainingService trainingService;
    private Producer producer;
    private MessageConverter messageConverter;

    @Operation( summary= "Save Training")
    @JmsListener(destination = "queue.saveTraining", containerFactory = "jmsListenerContainerFactory")
    public void saveTraining(Message message) {
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            TrainingRecord.TrainingRequest trainingRequest = messageConverter.convertMessageToObject(message, TrainingRecord.TrainingRequest.class);
            trainingService.saveTraining(trainingRequest);
            producer.sendMessage("queue.saveTraining.response", "Saved", processId);
            log.info("Saved Training {}", trainingRequest);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.saveTraining.response", "Not Saved", processId);
        }

    }


    @Operation( summary= "Update Training Status")
    @JmsListener(destination = "queue.updateTraining")
    public void updateTrainingStatusToCompleted(Message message){
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            Long trainingId = messageConverter.convertMessageToObject(message, Long.class);
            trainingService.updateTrainingStatusToCompleted(trainingId);
            producer.sendMessage("queue.updateTraining.response", "Updated", processId);
            log.info("Updated Training ID: {}", trainingId);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.updateTraining.response", "Not Updated", processId);
        }
    }

    @Operation(summary = "Get Training Summary by Trainer username")
    @JmsListener(destination = "queue.summaryTrainer")
    public void getTrainingSummaryByTrainerUsername(Message message){
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            String trainerUsername = messageConverter.convertMessageToObject(message, String.class);
            TrainingRecord.TrainerTrainingSummary response = trainingService.getTrainingSummaryByTrainer(trainerUsername).getBody();
            if (response != null) {
                producer.sendMessage("queue.summaryTrainer.response", response, processId);
                log.info("Trainer summary obtained from: {}", trainerUsername);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.summaryTrainer.response", ResponseEntity.notFound(), processId);
        }


    }

    @Operation(summary = "Get Training List by Trainer username, and Training Params")
    @JmsListener(destination = "queue.trainerTrainingList")
    public void getTrainerTrainingListByTrainingParams(Message message){
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            System.out.println(message);
            TrainingRecord.TrainingParamsRequest trainingParams = messageConverter.convertMessageToObject(message, TrainingRecord.TrainingParamsRequest.class);

            List<TrainingRecord.TrainerTrainingResponse> response = trainingService.getTrainerTrainingListByTrainingParams(trainingParams).getBody();
            if (response!=null && !response.isEmpty()) {
                log.info("Training List by Trainer obtained");
                producer.sendMessage("queue.trainerTrainingList.response", response, processId);
            }else {
                log.info("Training List by Trainer is empty");
                producer.sendMessage("queue.trainerTrainingList.response", new ArrayList<>(), processId);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.trainerTrainingList.response", new ArrayList<>(), processId);
        }

    }

    @Operation(summary = "Get Training List by Trainee username, and Training Params")
    @JmsListener(destination = "queue.traineeTrainingList")
    public void getTraineeTrainingListByTrainingParams(Message message){
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            TrainingRecord.TrainingParamsRequest trainingParams = messageConverter.convertMessageToObject(message, TrainingRecord.TrainingParamsRequest.class);

            List<TrainingRecord.TraineeTrainingResponse> response = trainingService.getTraineeTrainingListByTrainingParams(trainingParams).getBody();
            if (response!=null && !response.isEmpty()) {
                log.info("Training List by Trainee obtained");
                producer.sendMessage("queue.traineeTrainingList.response", response, processId);
            }else {
                log.info("Training List by Trainee is empty");
                producer.sendMessage("queue.traineeTrainingList.response", new ArrayList<>(), processId);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.traineeTrainingList.response", new ArrayList<>(), processId);
        }
    }

    @Operation(summary = "Delete Training By ID")
    @JmsListener(destination = "queue.deleteTraining")
    public void deleteTrainingById(Message message){
        String processId = null;
        try {
            processId = message.getStringProperty("processId");
            if (processId == null || processId.isEmpty()) {
                processId = "";
            }
            Long trainingId = messageConverter.convertMessageToObject(message, Long.class);
            ResponseEntity<Void> response = trainingService.deleteTrainingById(trainingId);
            if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                log.info("Deleted Training By ID {}", trainingId);
                producer.sendMessage("queue.deleteTraining.response", "Deleted", processId);
            }else{
                log.info("Not Deleted Training By ID {}", trainingId);
                producer.sendMessage("queue.deleteTraining.response", "Not Deleted", processId);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            producer.sendMessage("queue.deleteTraining.response", "Not Deleted", processId);
        }

    }

}
