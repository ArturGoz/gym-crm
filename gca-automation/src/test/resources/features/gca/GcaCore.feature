Feature: Trainee Registration
  As a new trainee
  I want to register with my details
  So that I can receive credentials for the system

  Scenario: Successful trainee registration
    When I send a registration request with the following data
      | firstName   | John           |
      | lastName    | Cena           |
      | dateOfBirth | 2000-01-15     |
      | address     | 123 Main Street|
    Then the trainee should be successfully registered

  Scenario: Failed trainee registration with invalid data
    When I send a registration request with the following data
      | firstName | |
      | lastName  | Cena |
    Then an error response should be returned