Feature: Trainer Workload Management
  As a system administrator
  I want to add/remove workloads and retrieve summaries for trainers
  So that I can track training durations accurately

  Background:
    Given a trainer exists with username "ronnie_coleman"

  Scenario: Successfully add workload for existing trainer and verify monthly summary
    When I add a workload of 200 minutes for "ronnie_coleman" in month 3 of 2025
    Then the trainer summary for "ronnie_coleman" should contain 200 minutes for month 3 of 2025

  Scenario: Successfully add workload for new trainer and verify monthly summary
    When I add a workload of 150 minutes for "new_trainer" in month 5 of 2025
    Then the trainer summary for "new_trainer" should contain 150 minutes for month 5 of 2025

  Scenario: Fail to add workload with invalid (negative) duration
    When I try to add workload of -50 minutes for "ronnie_coleman" in month 3 of 2025
    Then a validation exception should be thrown

  Scenario: Fail to retrieve summary for non-existent trainer
    When I request workload summary for "ghost_trainer" in month 1 of 2025
    Then a not found exception should be thrown