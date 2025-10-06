package com.gca.automation.component.integration.steps;

import com.gca.automation.component.GcaApiClient;
import com.gca.automation.component.TestContext;
import com.gca.automation.component.integration.IntegrationConfig;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = IntegrationConfig.class)
@Transactional
public class TrainerSteps {
    @Autowired
    private GcaApiClient client;

    @Autowired
    private TestContext context;

    @When("I get the workload for trainer {string} for year {int} and month {int}")
    public void iGetTrainerWorkload(String trainerUsername, int year, int month) {
        waitForData();

        ResponseEntity<Long> response = client.getTrainerWorkload(trainerUsername, year, month, context.getJwtToken());
        context.setLastResponse(response);
    }

    @Then("the trainer workload should include training with duration {int} for {string}")
    public void trainerWorkloadShouldIncludeTrainingWithDuration(int duration, String trainerUsername) {
        ResponseEntity<Long> response = (ResponseEntity<Long>) context.getLastResponse();

        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
        Assertions.assertThat(response.getBody())
                .as("Training duration in workload response for trainer " + trainerUsername)
                .isEqualTo(duration);
    }

    private void waitForData() {
        try {
            Thread.sleep(500);
        } catch (Exception ignored) {}
    }
}