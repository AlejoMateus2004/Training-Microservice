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
        // Llamar al método con el nombre de entrenador válido
        ResponseEntity<TrainingRecord.TrainerTrainingSummary> response = trainingService.getTrainingSummaryByTrainer("trainer.username");

        // Verificar que la respuesta sea OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verificar la estructura del resumen del entrenador
        TrainingRecord.TrainerTrainingSummary summary = response.getBody();
        assert summary != null;
        assertEquals(1, summary.summary().size()); // Debería haber datos para dos años

        // Verificar datos específicos
        Map<String, Long> summary2022 = summary.summary().get(2022);
        assertEquals(1, summary2022.size()); // Debería haber datos para dos meses en 2022
        assertEquals(Long.valueOf(105), summary2022.get("AUGUST"));
    }

    @Test
    public void testGetTrainingSummaryByTrainer_NoTrainings() {
        // Configurar comportamiento del mock para el método findTrainingByTrainer devolviendo una lista vacía
        when(trainingRepository.findTrainingByTrainer("trainer.username"))
                .thenReturn(new ArrayList<>());

        // Llamar al método con el nombre de entrenador válido
        ResponseEntity<TrainingRecord.TrainerTrainingSummary> response = trainingService.getTrainingSummaryByTrainer("trainer.username");

        // Verificar que la respuesta sea Not Found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testDeleteTrainingById_Success() {
        training.setTrainingIsCompleted(false);
        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(training));

        // Llamar al método con un ID de entrenamiento válido
        ResponseEntity<Void> response = trainingService.deleteTrainingById(1L);

        // Verificar que la respuesta sea OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verificar que se llamó al método deleteTrainingById del repositorio
        verify(trainingRepository, times(1)).deleteTrainingById(1L);
    }

    @Test
    public void testDeleteTrainingById_CompletedTraining() {
        when(trainingRepository.findById(1L)).thenReturn(java.util.Optional.of(training));

        // Llamar al método con un ID de entrenamiento de un entrenamiento completado
        ResponseEntity<Void> response = trainingService.deleteTrainingById(1L);

        // Verificar que la respuesta sea BadRequest
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Verificar que el método deleteTrainingById del repositorio no se haya llamado
        verify(trainingRepository, never()).deleteTrainingById(anyLong());
    }

    @Test
    public void testDeleteTrainingById_TrainingNotFound() {
        when(trainingRepository.findById(2L)).thenReturn(null);

        // Llamar al método con un ID de entrenamiento que no existe
        ResponseEntity<Void> response = trainingService.deleteTrainingById(2L);

        // Verificar que la respuesta sea InternalServerError
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Verificar que el método deleteTrainingById del repositorio no se haya llamado
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

        // Configurar comportamiento del mock para el método trainingToTrainerTrainingResponse
        when(trainingMapper.trainingToTrainerTrainingResponse(any(Training.class)))
                .thenReturn(new TrainingRecord.TrainerTrainingResponse(
                        1L,
                        "Plan Three Months",
                        training.getTrainingDate(),
                        training.getTraineeUsername(),
                        training.getTrainingDuration()
                ));
        // Crear objeto de solicitud de parámetros


        // Llamar al método con los parámetros de solicitud válidos
        ResponseEntity<List<TrainingRecord.TrainerTrainingResponse>> response = trainingService.getTrainerTrainingListByTrainingParams(request);

        // Verificar que la respuesta sea OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verificar que se llamó al método findTrainingByTrainerUsernameAndTrainingParams del repositorio
        verify(trainingRepository, times(1)).findTrainingByTrainerUsernameAndTrainingParams(
                request.trainerUsername(),
                request.periodFrom(),
                request.periodTo(),
                request.traineeUsername()
        );
        // Verificar que se llamó al método trainingToTrainerTrainingResponse del mapper para cada entrenamiento
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

        // Configurar comportamiento del mock para el método trainingToTrainerTrainingResponse
        when(trainingMapper.trainingToTraineeTrainingResponse(any(Training.class)))
                .thenReturn(new TrainingRecord.TraineeTrainingResponse(
                        1L,
                        "Plan Three Months",
                        training.getTrainingDate(),
                        training.getTrainerUsername(),
                        training.getTrainingDuration()
                ));
        // Crear objeto de solicitud de parámetros


        // Llamar al método con los parámetros de solicitud válidos
        ResponseEntity<List<TrainingRecord.TraineeTrainingResponse>> response = trainingService.getTraineeTrainingListByTrainingParams(request);

        // Verificar que la respuesta sea OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verificar que se llamó al método findTrainingByTrainerUsernameAndTrainingParams del repositorio
        verify(trainingRepository, times(1)).findTrainingByTraineeUsernameAndTrainingParams(
                request.traineeUsername(),
                request.periodFrom(),
                request.periodTo(),
                request.trainerUsername()
        );
        // Verificar que se llamó al método trainingToTrainerTrainingResponse del mapper para cada entrenamiento
        verify(trainingMapper, times(1)).trainingToTraineeTrainingResponse(any(Training.class));
    }
}