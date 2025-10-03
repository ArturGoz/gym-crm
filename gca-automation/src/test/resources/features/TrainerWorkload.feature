Feature: Trainer Workload Management via Messaging
  As a system administrator
  I want to add/remove workloads and retrieve summaries for trainers
  So that I can track training durations accurately in MongoDB

  Background:
    Given a trainer exists with username "ronnie_coleman"

  Scenario: Successfully add workload for existing trainer and verify monthly summary
    When I add a workload of 200 minutes for "ronnie_coleman" in month 3 of 2025 year
    Then the trainer summary for "ronnie_coleman" should contain 200 minutes for month 3 of 2025 year

  Scenario: Fail to add workload with invalid (negative) duration
    When I add a workload of -50 minutes for "ronnie_coleman" in month 3 of 2025 year
    Then no workload should exist for "ronnie_coleman" in month 3 of 2025 year

  Scenario: Successfully add workload for new trainer and verify monthly summary
    When I add workload
      | trainerUsername | minutes | month | year |
      | new_trainer     | 150     | 5     | 2025 |
    Then the trainer summary should contain
      | trainerUsername | minutes | month | year |
      | new_trainer     | 150     | 5     | 2025 |