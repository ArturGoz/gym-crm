package com.gca.automation.component.gca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gca.automation.component.GcaApiClient;
import com.gca.automation.dto.AuthCredentials;
import com.gca.automation.dto.ErrorResponse;
import com.gca.automation.dto.TraineeRegistrationRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = GcaCoreServiceSteps.class)
public class GcaCoreServiceSteps {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private GcaApiClient apiHelper;

    private ResponseEntity<String> response;

    @When("I send a registration request with the following data")
    public void iSendRegistrationRequest(Map<String, String> data) {
        TraineeRegistrationRequest request = buildTraineeRegistrationRequest(data);

        response = apiHelper.registerTraineeRaw(request);
    }

    @Then("the trainee should be successfully registered")
    public void traineeShouldBeRegistered() throws Exception {
        AuthCredentials registrationData = parseResponse(response.getBody(), AuthCredentials.class);

        assertThat(response.getStatusCode().value())
                .as("Registration should return 200 status")
                .isEqualTo(200);
        assertThat(registrationData.getUsername())
                .as("Username should not be empty")
                .isNotEmpty();
        assertThat(registrationData.getPassword())
                .as("Password should not be empty")
                .isNotEmpty();
    }

    @Then("an error response should be returned")
    public void errorResponseShouldBeReturned() throws Exception {
        ErrorResponse errorData = parseResponse(response.getBody(), ErrorResponse.class);

        assertThat(response.getStatusCode().value())
                .as("Error response should return 400 status")
                .isEqualTo(400);
        assertThat(errorData.getErrorCode())
                .as("Error code should be greater than 0")
                .isGreaterThan(0);
        assertThat(errorData.getErrorMessage())
                .as("Error message should not be empty")
                .isNotEmpty();
    }

    private <T> T parseResponse(String jsonResponse, Class<T> responseType) throws Exception {
        JsonNode json = objectMapper.readTree(jsonResponse);

        return objectMapper.treeToValue(json, responseType);
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
