package com.gca.automation.component.integration.steps;

import com.gca.automation.component.GcaApiClient;
import com.gca.automation.component.TestContext;
import com.gca.automation.component.integration.IntegrationConfig;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = IntegrationConfig.class)
@Transactional
public class TraineeSteps {

    @Autowired
    private GcaApiClient client;

    @Autowired
    private TestContext context;

    @When("I retrieve the trainee profile for the registered username")
    public void iRetrieveTheTraineeProfileForTheRegisteredUsername() {
        Assertions.assertThat(context.getCredentials())
                .as("Registered credentials should be available")
                .isNotNull();
        Assertions.assertThat(context.getJwtToken())
                .as("JWT token should be available")
                .isNotNull();

        ResponseEntity<Map> profileResponse = client.getTraineeProfile(
                context.getCredentials().getUsername(),
                context.getJwtToken()
        );
        context.setLastResponse(profileResponse);
    }

    @When("I attempt to retrieve the trainee profile for username {string} without authentication")
    public void iAttemptToRetrieveTheTraineeProfileForUsernameWithoutAuthentication(String username) {
        ResponseEntity<Map> profileResponse = client.getTraineeProfile(username, null);
        context.setLastResponse(profileResponse);
    }
}