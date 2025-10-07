package com.gca.automation.component.integration.steps;

import com.gca.automation.component.TestContext;
import com.gca.automation.component.integration.IntegrationConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = IntegrationConfig.class)
public class ResponseHandlerSteps {

    @Autowired
    private TestContext context;

    @Then("the response status code should be {int}")
    public void theProfileResponseStatusCodeShouldBe(int expectedStatusCode) {
        ResponseEntity<Map> response = (ResponseEntity<Map>) context.getLastResponse();

        Assertions.assertThat(response)
                .as("Profile response should not be null")
                .isNotNull();
        Assertions.assertThat(response.getStatusCode().value())
                .as("Profile response status code")
                .isEqualTo(expectedStatusCode);
    }

    @And("the profile response should contain the trainee's first name {string}")
    public void theProfileResponseShouldContainTheTraineesFirstName(String expectedFirstName) {
        ResponseEntity<Map> response = (ResponseEntity<Map>) context.getLastResponse();

        Assertions.assertThat(response.getBody())
                .as("Profile response body should not be null")
                .isNotNull();
        Assertions.assertThat(response.getBody().get("firstName"))
                .as("First name in profile response")
                .isEqualTo(expectedFirstName);
    }

    @And("I should receive an error response with errorCode")
    public void iShouldReceiveAnErrorResponseWithErrorCode() {
        ResponseEntity<Map> response = (ResponseEntity<Map>) context.getLastResponse();
        Map<String, Object> errorBody = response.getBody();

        Assertions.assertThat(errorBody)
                .as("Error response body should not be null")
                .isNotNull()
                .containsKey("errorCode");
        Assertions.assertThat(errorBody.get("errorCode"))
                .as("Error code should not be null")
                .isNotNull();
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        ResponseEntity<Map> response = (ResponseEntity<Map>) context.getLastResponse();
        Map<String, Object> errorBody = response.getBody();

        Assertions.assertThat(errorBody)
                .as("Error response body should not be null")
                .isNotNull()
                .containsKey("errorMessage");
        Assertions.assertThat((String) errorBody.get("errorMessage"))
                .as("Error message")
                .containsIgnoringCase(expectedMessage);
    }
}