package com.training_microservice.cucumber;

import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.dao.TrainingRepository;
import com.training_microservice.domain.entities.Training;
import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.mapper.TrainingMapperImpl;
import com.training_microservice.service.TrainingService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class TrainingServiceSteps {
    @Mock
    private TrainingRepo trainingRepository;
    private TrainingService trainingService;
    private TrainingRecord.TrainingRequest trainingRequest;
    private ResponseEntity response;
    private TrainingRecord.TrainingParamsRequest request;
    private Training training;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        trainingService = new TrainingService(new TrainingMapperImpl(), trainingRepository);

        training =  new Training();
        training.setId(1L);
        training.setTrainingTypeId(1L);
        training.setTraineeUsername("trainee.username");
        training.setTrainerUsername("trainer.username");
        training.setTrainingDate(LocalDate.parse("2022-08-06"));
        training.setTrainingDuration(3L);
        training.setTrainingName("Plan Three Months");
        training.setTrainingIsCompleted(false);
    }

    @Given("The user types a valid training request")
    public void theUserTypesAValidTrainingRequest() {
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );

    }

    @When("The user saves the training request")
    public void theUserSavesTheTrainingRequest() {
        Mockito.when(trainingRepository.save(any(Training.class))).thenReturn(training);
        response = trainingService.saveTraining(trainingRequest);
    }

    @Then("The training should be created successfully")
    public void theTrainingShouldBeCreatedSuccessfully() {
        assertNotNull(response);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Given("The user types an invalid training request")
    public void theUserTypesAnInvalidTrainingRequest() {
        trainingRequest = new TrainingRecord.TrainingRequest(
                null,
                null,
                null,
                null,
                null
        );
    }

    @When("The user tries to save the invalid training request")
    public void theUserTriesToSaveTheInvalidTrainingRequest() {
        response = trainingService.saveTraining(trainingRequest);
    }

    @Then("The training should not be created and an error message should be returned")
    public void theTrainingShouldNotBeCreatedAndAnErrorMessageShouldBeReturned() {
        assertNotNull(response);
        assertEquals(ResponseEntity.badRequest().build(), response);
    }
    
/////////////////////////////  
    
    @Given("There is an existing training record")
    public void thereIsAnExistingTrainingRecord() {
    }

    @When("The user requests to update the training status to completed with valid id")
    public void theUserRequestsToUpdateTheTrainingStatusToCompletedWithValidId() {
        Mockito.when(trainingRepository.findById(1L)).thenReturn(Optional.ofNullable(training));
        training.setTrainingIsCompleted(true);
        Mockito.when(trainingRepository.save(any(Training.class))).thenReturn(training);
        response = trainingService.updateTrainingStatusToCompleted(1L);
    }

    @Then("The training status should be updated to completed")
    public void theTrainingStatusShouldBeUpdatedToCompleted() {
        assertNotNull(response);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Given("The user tries to update a training record that does not exist")
    public void theUserTriesToUpdateATrainingRecordThatDoesNotExist() {
    }

    @When("The user requests to update the training status to completed with invalid id")
    public void theUserRequestsToUpdateTheTrainingStatusToCompletedWithInvalidId() {
        response = trainingService.updateTrainingStatusToCompleted(1L);
    }

    @Then("An error message indicating that the training does not exist should be returned")
    public void anErrorMessageIndicatingThatTheTrainingDoesNotExistShouldBeReturned() {
        assertNotNull(response);
        assertEquals(ResponseEntity.notFound().build(), response);
    }
/////////////////////////////  

    @Given("That user has multiple training records")
    public void thatUserHasMultipleTrainingRecords() {
    }

    @When("The user requests his workload summary by trainer username {string}")
    public void theUserRequestsHisWorkloadSummaryByTrainerUsername(String username) {
        if (username.equals("trainer.username")) {
            training.setTrainingIsCompleted(true);
            Mockito.when(trainingRepository.findTrainingByTrainer("trainer.username")).thenReturn(Collections.singletonList(training));
        }
        response = trainingService.getTrainingSummaryByTrainer("trainer.username");
    }

    @Then("The user should receive a workload summary")
    public void theUserShouldReceiveAWorkloadSummary() {
        Map<Integer, Map<String, Long>> summary = new HashMap<>();
        Map<String, Long> month = new HashMap<>();
        month.put("AUGUST",3L);
        summary.put(2022, month);

        TrainingRecord.TrainerTrainingSummary expected =new TrainingRecord.TrainerTrainingSummary(
                summary
        );
        assertNotNull(response);
        assertEquals(ResponseEntity.ok().body(expected), response);
    }

    @Given("That the user has no training records")
    public void thatTheUserHasNoTrainingRecords() {
        //No Training Record
    }

    @Then("An error message indicating that there are no training records should be returned")
    public void anErrorMessageIndicatingThatThereAreNoTrainingRecordsShouldBeReturned() {
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
/////////////////////////////  

    @Given("There is an existing training record with an id")
    public void thereIsAnExistingTrainingRecordWithAnId() {
    }

    @When("The user requests to delete the training by id")
    public void theUserRequestsToDeleteTheTrainingById() {
        Mockito.when(trainingRepository.findById(1L)).thenReturn(Optional.ofNullable(training));
        response = trainingService.deleteTrainingById(1L);
    }

    @Then("The training with that id should be deleted successfully")
    public void theTrainingWithThatIdShouldBeDeletedSuccessfully() {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Given("There is no existing training record with an id")
    public void thereIsNoExistingTrainingRecordWithAnId() {
        //There is no existing training record with an id
    }

    @When("The user requests to delete the training by this id")
    public void theUserRequestsToDeleteTheTrainingByThisId() {
        response = trainingService.deleteTrainingById(1L);
    }
/////////////////////////////  
    @Given("The user has multiple training records")
    public void theUserHasMultipleTrainingRecords() {
        request = new TrainingRecord.TrainingParamsRequest(
                LocalDate.parse("2022-08-06"),
                LocalDate.now(),
                "trainer.username",
                "trainee.username"

        );
    }

    @When("The user requests his training list by training params")
    public void theUserRequestsHisTrainingListByTrainingParams() {
        Mockito.when(trainingRepository.findTrainingByTrainerUsernameAndTrainingParams("trainer.username", request.periodFrom(), request.periodTo(), request.traineeUsername())).thenReturn(Collections.singletonList(training));
        response = trainingService.getTrainerTrainingListByTrainingParams(request);
    }

    @Then("The user should receive his training list")
    public void theUserShouldReceiveHisTrainingList() {
        TrainingRecord.TrainerTrainingResponse expectedResponse = new TrainingRecord.TrainerTrainingResponse(
                1L,
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                "trainee.username",
                3L
        );
        List<TrainingRecord.TrainerTrainingResponse> responseList = new ArrayList<>();
        responseList.add(expectedResponse);

        assertNotNull(response);
        assertEquals(ResponseEntity.ok(responseList), response);
    }

    @Given("The user has no training records")
    public void theUserHasNoTrainingRecords() {
        request = new TrainingRecord.TrainingParamsRequest(
                LocalDate.parse("2022-08-06"),
                LocalDate.now(),
                "invalid.username",
                "trainee.username"

        );
    }

}