Feature: Integration of Trainee, Trainer and Workload microservices

  Scenario: Create trainee and trainer, login, create training, verify trainer workload updated
    Given I register a new trainee with the following details:
      | firstName | lastName | dateOfBirth | address         |
      | John      | Cena     | 2000-01-15  | 123 Main Street |
    And I register a new trainer with the following details:
      | firstName | lastName | specialization |
      | Yuriy     | Boika    | Boxing         |
    When I login with the registered trainee credentials
    And I create a new training with the following details:
      | traineeUsername | trainerUsername | trainingName | trainingDate | duration |
      | john.cena       | yuriy.boika     | Boxing       | 2027-07-15   | 60       |
    Then the response status code should be 200
    When I get the workload for trainer "yuriy.donets" for year 2027 and month 7
    Then the trainer workload should include training with duration 60 for "yuriy.donets"

  Scenario: Attempt to create training with non-existent trainer
    Given I register a new trainee with the following details:
      | firstName | lastName | dateOfBirth | address         |
      | John      | Cena     | 2000-01-15  | 123 Main Street |
    And I register a new trainer with the following details:
      | firstName | lastName | specialization |
      | Yuriy     | Boika    | Boxing         |
    When I login with the registered trainee credentials
    And I create a new training with the following details:
      | traineeUsername | trainerUsername | trainingName | trainingDate | duration |
      | john.cena       | yuriy111.boika  | Boxing       | 2027-07-15   | 60       |
    Then the response status code should be 404
    And I should receive an error response with errorCode