package com.training_microservice.service;

import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.domain.entities.Training;
import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.mapper.TrainingMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepo trainingRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingService trainingService;
    private Training training;
    private TrainingRecord.TrainingRequest trainingRequest;
    @BeforeEach
    void setUp() {
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );
        training = new Training();

        training.setTrainingTypeId(1L);
        training.setTraineeUsername("trainee.username");
        training.setTrainerUsername("trainer.username");
        training.setTrainingDate(LocalDate.parse("2022-08-06"));
        training.setTrainingDuration(105L);
        training.setTrainingName("Plan Three Months");
        training.setTrainingIsCompleted(true);

    }



    @DisplayName("Test save Training")
    @Test
    void testSaveTraining() {
        when(trainingMapper.trainingRequestToTraining(trainingRequest)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(training);

        ResponseEntity response = trainingService.saveTraining(trainingRequest);
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @DisplayName("Test that throw an exception and the result is null while saving the Training")
    @Test
    void saveTraining_ExceptionThrown_LogsErrorAndReturnsBadRequestStatus() {
        when(trainingMapper.trainingRequestToTraining(trainingRequest)).thenReturn(null);

        // Act
        ResponseEntity responseEntity = trainingService.saveTraining(trainingRequest);

        // Assert
        assertEquals(ResponseEntity.badRequest().build(), responseEntity);
        verify(trainingMapper, times(1)).trainingRequestToTraining(trainingRequest);
        verifyNoInteractions(trainingRepository);
    }

    @Test
    public void testUpdateTrainingStatusToCompleted_Success() {
        training.setTrainingIsCompleted(false);

        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(training));

        when(trainingRepository.save(training)).thenReturn(training);

        ResponseEntity response = trainingService.updateTrainingStatusToCompleted(1L);
        // Verify that ResponseEntity is OK
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    public void testUpdateTrainingStatusToCompleted_AlreadyCompleted() {
        // Mocking behavior for getTrainingIsCompleted method
        Training completedTraining = new Training();
        completedTraining.setTrainingIsCompleted(true);
        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(completedTraining));

        // Call the method with a valid trainingId
        ResponseEntity response = trainingService.updateTrainingStatusToCompleted(1L);
        // Verify that ResponseEntity is OK
        assertEquals(ResponseEntity.ok().build(), response);
        // Verify that save method is not called
        verify(trainingRepository, never()).save(any(Training.class));
    }

    @Test
    public void testUpdateTrainingStatusToCompleted_Failure() {

        // Mocking behavior for findById method returning null
        when(trainingRepository.findById(2L)).thenReturn(null);

        // Call the method with an invalid trainingId
        ResponseEntity response = trainingService.updateTrainingStatusToCompleted(2L);
        // Verify that ResponseEntity is BadRequest
        assertEquals(ResponseEntity.badRequest().build(), response);
    }

    @Test
    public void testGetTrainingSummaryByTrainer_Success() {
        when(trainingRepository.findTrainingByTrainer("trainer.username"))
                .thenReturn(Collections.singletonList(training));

        ResponseEntity<TrainingRecord.TrainerTrainingSummary> response = trainingService.getTrainingSummaryByTrainer("trainer.username");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TrainingRecord.TrainerTrainingSummary summary = response.getBody();
        assert summary != null;
        assertEquals(1, summary.summary().size());

        Map<String, Long> summary2022 = summary.summary().get(2022);
        assertEquals(1, summary2022.size());
        assertEquals(Long.valueOf(105), summary2022.get("AUGUST"));
    }

    @Test
    public void testGetTrainingSummaryByTrainer_NoTrainings() {
        when(trainingRepository.findTrainingByTrainer("trainer.username"))
                .thenReturn(new ArrayList<>());

        ResponseEntity<TrainingRecord.TrainerTrainingSummary> response = trainingService.getTrainingSummaryByTrainer("trainer.username");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testDeleteTrainingById_Success() {
        training.setTrainingIsCompleted(false);
        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(training));

        ResponseEntity<Void> response = trainingService.deleteTrainingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainingRepository, times(1)).deleteTrainingById(1L);
    }

    @Test
    public void testDeleteTrainingById_CompletedTraining() {
        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(training));

        ResponseEntity<Void> response = trainingService.deleteTrainingById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(trainingRepository, never()).deleteTrainingById(anyLong());
    }

    @Test
    public void testDeleteTrainingById_TrainingNotFound() {
        when(trainingRepository.findById(2L)).thenReturn(null);

        ResponseEntity<Void> response = trainingService.deleteTrainingById(2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(trainingRepository, never()).deleteTrainingById(anyLong());
    }

    @Test
    public void testGetTrainerTrainingListByTrainingParams_Success() {
        TrainingRecord.TrainingParamsRequest request = new TrainingRecord.TrainingParamsRequest(
                LocalDate.parse("2022-08-06"),
                LocalDate.now(),
                "trainer.username",
                "trainee.username"

        );
        when(trainingRepository.findTrainingByTrainerUsernameAndTrainingParams(
                request.trainerUsername(),
                request.periodFrom(),
                request.periodTo(),
                request.traineeUsername()
            )
        ).thenReturn(Collections.singletonList(training));

        when(trainingMapper.trainingToTrainerTrainingResponse(any(Training.class)))
                .thenReturn(new TrainingRecord.TrainerTrainingResponse(
                        1L,
                        "Plan Three Months",
                        training.getTrainingDate(),
                        training.getTraineeUsername(),
                        training.getTrainingDuration()
                ));


        ResponseEntity<List<TrainingRecord.TrainerTrainingResponse>> response = trainingService.getTrainerTrainingListByTrainingParams(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainingRepository, times(1)).findTrainingByTrainerUsernameAndTrainingParams(
                request.trainerUsername(),
                request.periodFrom(),
                request.periodTo(),
                request.traineeUsername()
        );
        verify(trainingMapper, times(1)).trainingToTrainerTrainingResponse(any(Training.class));
    }

    @Test
    public void testGetTraineeTrainingListByTrainingParams_Success() {
        TrainingRecord.TrainingParamsRequest request = new TrainingRecord.TrainingParamsRequest(
                LocalDate.parse("2022-08-06"),
                LocalDate.now(),
                "trainer.username",
                "trainee.username"

        );
        when(trainingRepository.findTrainingByTraineeUsernameAndTrainingParams(
                        request.traineeUsername(),
                        request.periodFrom(),
                        request.periodTo(),
                        request.trainerUsername()
                )
        ).thenReturn(Collections.singletonList(training));

        when(trainingMapper.trainingToTraineeTrainingResponse(any(Training.class)))
                .thenReturn(new TrainingRecord.TraineeTrainingResponse(
                        1L,
                        "Plan Three Months",
                        training.getTrainingDate(),
                        training.getTrainerUsername(),
                        training.getTrainingDuration()
                ));

        ResponseEntity<List<TrainingRecord.TraineeTrainingResponse>> response = trainingService.getTraineeTrainingListByTrainingParams(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trainingRepository, times(1)).findTrainingByTraineeUsernameAndTrainingParams(
                request.traineeUsername(),
                request.periodFrom(),
                request.periodTo(),
                request.trainerUsername()
        );
        verify(trainingMapper, times(1)).trainingToTraineeTrainingResponse(any(Training.class));
    }
}