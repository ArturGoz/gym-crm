package com.gca.automation.component.integration;

import com.gca.automation.component.GcaApiClient;
import com.gca.automation.dto.AuthCredentials;
import com.gca.automation.dto.TraineeRegistrationRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = IntegrationConfig.class)
public class LoginSteps {

    @Autowired
    private GcaApiClient gcaApiClient;

    private ResponseEntity<Map> response;
    private AuthCredentials registeredCredentials;

    @Given("I register a new trainee with the following details:")
    public void iRegisterANewTrainee(DataTable dataTable) {
        Map<String, String> traineeData = dataTable.asMaps().get(0);
        TraineeRegistrationRequest request = buildTraineeRegistrationRequest(traineeData);

        ResponseEntity<Map> registerResponse = gcaApiClient.registerTrainee(request);
        assertThat(registerResponse.getStatusCode().value()).isIn(200, 201);
        assertThat(registerResponse.getBody()).isNotNull();

        registeredCredentials = extractCredentials(registerResponse.getBody());
        validateCredentials(registeredCredentials);
    }

    @When("I login with the registered trainee credentials")
    public void iLoginWithTheRegisteredTraineeCredentials() {
        assertThat(registeredCredentials)
                .as("Registered credentials should be available")
                .isNotNull();

        response = gcaApiClient.login(
                registeredCredentials.getUsername(),
                registeredCredentials.getPassword()
        );
    }

    @When("I login with the following credentials:")
    public void iLoginWithTheFollowingCredentials(DataTable dataTable) {
        Map<String, String> credentials = dataTable.asMaps().get(0);
        String username = credentials.get("username");
        String password = credentials.getOrDefault("password", "");

        response = gcaApiClient.login(username, password);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        assertThat(response)
                .as("Response should not be null")
                .isNotNull();
        assertThat(response.getStatusCode().value())
                .as("Response status code")
                .isEqualTo(expectedStatusCode);
    }

    @And("I should receive a valid authentication token")
    public void iShouldReceiveAValidAuthenticationToken() {
        assertThat(response)
                .as("Response should not be null")
                .isNotNull();
        assertThat(extractJwtToken(response))
                .as("JWT token should be present and not empty")
                .isNotEmpty();
    }

    @And("I should receive an error response with errorCode")
    public void iShouldReceiveAnErrorResponseWithErrorCode() {
        Map<String, Object> errorBody = response.getBody();

        assertThat(errorBody)
                .as("Error response body should not be null")
                .isNotNull()
                .containsKey("errorCode");
        assertThat(errorBody.get("errorCode"))
                .as("Error code should not be null")
                .isNotNull();
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        Map<String, Object> errorBody = response.getBody();

        assertThat(errorBody)
                .as("Error response body should not be null")
                .isNotNull()
                .containsKey("errorMessage");
        assertThat((String) errorBody.get("errorMessage"))
                .as("Error message")
                .containsIgnoringCase(expectedMessage);
    }

    private TraineeRegistrationRequest buildTraineeRegistrationRequest(Map<String, String> dataTable) {
        return TraineeRegistrationRequest.builder()
                .firstName(dataTable.get("firstName"))
                .lastName(dataTable.get("lastName"))
                .dateOfBirth(dataTable.get("dateOfBirth"))
                .address(dataTable.get("address"))
                .build();
    }

    private AuthCredentials extractCredentials(Map<String, Object> responseBody) {
        return AuthCredentials.builder()
                .username((String) responseBody.get("username"))
                .password((String) responseBody.get("password"))
                .build();
    }

    private void validateCredentials(AuthCredentials credentials) {
        assertThat(credentials.getUsername())
                .as("Username should not be empty")
                .isNotEmpty();
        assertThat(credentials.getPassword())
                .as("Password should not be empty")
                .isNotEmpty();
    }

    private String extractJwtToken(ResponseEntity<Map> response) {
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookies)
                .as("Set-Cookie headers should be present")
                .isNotEmpty();

        return cookies.stream()
                .filter(cookie -> cookie.startsWith("JWT="))
                .findFirst()
                .map(cookie -> cookie.split(";")[0].substring("JWT=".length()))
                .orElseThrow(() -> new AssertionError("JWT cookie not found"));
    }
}