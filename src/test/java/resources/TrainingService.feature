Feature: Training management

  Scenario: The user saves a new training record successfully
    Given The user types a valid training request
    When The user saves the training request
    Then The training should be created successfully

  Scenario: The user tries to save an invalid training record
    Given The user types an invalid training request
    When The user tries to save the invalid training request
    Then The training should not be created and an error message should be returned

  Scenario: The user updates the training status to completed
    Given There is an existing training record
    When The user requests to update the training status to completed with valid id
    Then The training status should be updated to completed

  Scenario: The user tries to update the training status of a non-existing record
    Given The user tries to update a training record that does not exist
    When The user requests to update the training status to completed with invalid id
    Then An error message indicating that the training does not exist should be returned

  Scenario: The user gets his workload summary
    Given That user has multiple training records
    When The user requests his workload summary by trainer username "trainer.username"
    Then The user should receive a workload summary

  Scenario: The user tries to get a workload summary with no training records
    Given That the user has no training records
    When The user requests his workload summary by trainer username "trainer2.username"
    Then An error message indicating that there are no training records should be returned

  Scenario: The user deletes a training by id
    Given There is an existing training record with an id
    When The user requests to delete the training by id
    Then The training with that id should be deleted successfully

  Scenario: The user tries to delete a training record by an invalid id
    Given There is no existing training record with an id
    When The user requests to delete the training by this id
    Then An error message indicating that the training does not exist should be returned

  Scenario: The user gets their training list
    Given The user has multiple training records
    When The user requests his training list by training params
    Then The user should receive his training list

  Scenario: The user tries to get a training list with no training records
    Given The user has no training records
    When The user requests his training list by training params
    Then An error message indicating that there are no training records should be returned
