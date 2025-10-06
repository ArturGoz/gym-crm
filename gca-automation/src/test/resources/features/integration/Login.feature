Feature: User Authentication
  As a user
  I want to login to the system
  So that I can access protected resources

  Scenario: Successful login with valid credentials
    Given I register a new trainee with the following details:
      | firstName   | lastName | dateOfBirth | address          |
      | John        | Cena     | 2000-01-15  | 123 Main Street  |
    When I login with the registered trainee credentials
    Then the response status code should be 200

  Scenario: Failed login with invalid credentials
    When I login with invalid credentials
      | username     | password        |
      | invalid.user | wrongpassword   |
    Then the response status code should be 404
    And I should receive an error response with errorCode
    And the error message should contain "User not found"