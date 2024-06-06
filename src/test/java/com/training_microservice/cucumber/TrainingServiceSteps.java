package com.training_microservice.cucumber;

import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.service.TrainingService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainingServiceSteps {

    @Autowired
    private TrainingService trainingService;

    private TrainingRecord.TrainingRequest trainingRequest;
    private ResponseEntity response;

    @Given("The user types a valid training request")
    public void the_user_types_a_valid_training_request() {
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );

    }

    @When("The user saves the training request")
    public void the_user_saves_the_training_request() {
        response = trainingService.saveTraining(trainingRequest);
    }

    @Then("The training should be created successfully")
    public void the_training_should_be_created_successfully() {
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
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );
        trainingService.saveTraining(trainingRequest);
    }

    @When("The user requests to update the training status to completed")
    public void theUserRequestsToUpdateTheTrainingStatusToCompleted() {
        response = trainingService.updateTrainingStatusToCompleted(1L);
    }

    @Then("The training status should be updated to completed")
    public void theTrainingStatusShouldBeUpdatedToCompleted() {
        assertNotNull(response);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Given("The user tries to update a training record that does not exist")
    public void theUserTriesToUpdateATrainingRecordThatDoesNotExist() {
        //Training record does not exist
    }

    @Then("An error message indicating that the training does not exist should be returned")
    public void anErrorMessageIndicatingThatTheTrainingDoesNotExistShouldBeReturned() {
        assertNotNull(response);
        assertEquals(ResponseEntity.notFound().build(), response);
    }
/////////////////////////////  

    @Given("That user has multiple training records")
    public void thatUserHasMultipleTrainingRecords() {
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );
        trainingService.saveTraining(trainingRequest);
        trainingService.updateTrainingStatusToCompleted(1L);
    }

    @When("The user requests his workload summary")
    public void theUserRequestsHisWorkloadSummary() {
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
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );
        trainingService.saveTraining(trainingRequest);
    }

    @When("The user requests to delete the training by id")
    public void theUserRequestsToDeleteTheTrainingById() {
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
        trainingRequest = new TrainingRecord.TrainingRequest(
                "trainee.username",
                "trainer.username",
                "Plan Three Months",
                LocalDate.parse("2022-08-06"),
                3L
        );
        trainingService.saveTraining(trainingRequest);
    }

    @When("The user requests his training list by training params")
    public void the_user_requests_his_training_list_by_training_params() {
        TrainingRecord.TrainingParamsRequest request = new TrainingRecord.TrainingParamsRequest(
                LocalDate.parse("2022-08-06"),
                LocalDate.now(),
                "trainer.username",
                "trainee.username"

        );
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
        //The user has no training records
    }
}