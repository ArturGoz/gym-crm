package com.gca.automation.component.gca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.automation.component.GcaApiClient;
import com.gca.automation.component.TestContext;
import com.gca.automation.dto.AuthCredentials;
import com.gca.automation.dto.ErrorResponse;
import com.gca.automation.dto.TraineeRegistrationRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = GcaCoreConfig.class)
public class GcaCoreServiceSteps {

    @Autowired
    private GcaApiClient apiClient;

    @Autowired
    private TestContext testContext;

    @When("I send a registration request with the following data")
    public void iSendRegistrationRequest(Map<String, String> data) {
        TraineeRegistrationRequest request = buildTraineeRegistrationRequest(data);
        ResponseEntity<Map> response = apiClient.registerTrainee(request);

        testContext.setLastResponse(response);
    }

    @Then("the trainee should be successfully registered")
    public void traineeShouldBeRegistered() {
        ResponseEntity<Map> response = (ResponseEntity<Map>) testContext.getLastResponse();
        AuthCredentials registrationData = extractCredentials(response.getBody());

        Assertions.assertThat(response.getStatusCode().value())
                .as("Registration should return 200 or 201 status")
                .isIn(200, 201);
        Assertions.assertThat(registrationData.getUsername())
                .as("Username should not be empty")
                .isNotEmpty();
        Assertions.assertThat(registrationData.getPassword())
                .as("Password should not be empty")
                .isNotEmpty();

        testContext.setCredentials(registrationData);
    }

    @Then("an error response should be returned")
    public void errorResponseShouldBeReturned() {
        ResponseEntity<Map> response = (ResponseEntity<Map>) testContext.getLastResponse();
        ErrorResponse errorData = extractErrorResponse(response.getBody());

        Assertions.assertThat(response.getStatusCode().value())
                .as("Error response should return 400 status")
                .isEqualTo(400);
        Assertions.assertThat(errorData.getErrorCode())
                .as("Error code should be greater than 0")
                .isGreaterThan(0);
        Assertions.assertThat(errorData.getErrorMessage())
                .as("Error message should not be empty")
                .isNotEmpty();
    }

    private AuthCredentials extractCredentials(Map<String, Object> responseBody) {
        return AuthCredentials.builder()
                .username((String) responseBody.get("username"))
                .password((String) responseBody.get("password"))
                .build();
    }

    private ErrorResponse extractErrorResponse(Map<String, Object> responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(responseBody, ErrorResponse.class);
    }

    private TraineeRegistrationRequest buildTraineeRegistrationRequest(Map<String, String> dataTable) {
        return TraineeRegistrationRequest.builder()
                .firstName(dataTable.get("firstName"))
                .lastName(dataTable.get("lastName"))
                .dateOfBirth(dataTable.get("dateOfBirth"))
                .address(dataTable.get("address"))
                .build();
    }
}