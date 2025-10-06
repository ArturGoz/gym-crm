package com.gca.automation.component.integration.steps;

import com.gca.automation.component.GcaApiClient;
import com.gca.automation.component.TestContext;
import com.gca.automation.component.integration.IntegrationConfig;
import com.gca.automation.dto.AuthCredentials;
import com.gca.automation.dto.TraineeRegistrationRequest;
import com.gca.automation.dto.TrainerRegistrationRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = IntegrationConfig.class)
@Transactional
public class AuthSteps {

    @Autowired
    private GcaApiClient client;

    @Autowired
    private TestContext context;

    @Given("I register a new trainee with the following details:")
    public void iRegisterANewTrainee(DataTable dataTable) {
        Map<String, String> traineeData = dataTable.asMaps().get(0);
        TraineeRegistrationRequest request = buildTraineeRegistrationRequest(traineeData);

        ResponseEntity<Map> registerResponse = client.registerTrainee(request);
        Assertions.assertThat(registerResponse.getStatusCode().value()).isIn(200, 201);
        Assertions.assertThat(registerResponse.getBody()).isNotNull();

        context.setLastResponse(registerResponse);
        context.setCredentials(extractCredentials(registerResponse.getBody()));
    }

    @Given("I register a new trainer with the following details:")
    public void iRegisterANewTrainer(DataTable dataTable) {
        Map<String, String> trainerData = dataTable.asMaps().get(0);
        TrainerRegistrationRequest request = TrainerRegistrationRequest.builder()
                .firstName(trainerData.get("firstName"))
                .lastName(trainerData.get("lastName"))
                .specialization(trainerData.get("specialization"))
                .build();

        ResponseEntity<Map> response = client.registerTrainer(request);
        Assertions.assertThat(response.getStatusCode().value()).isIn(200, 201);

        context.setLastResponse(response);
    }

    @When("I login with invalid credentials")
    public void iLoginWithTheFollowingCredentials(DataTable dataTable) {
        Map<String, String> credentials = dataTable.asMaps().get(0);
        String username = credentials.get("username");
        String password = credentials.getOrDefault("password", "");

        ResponseEntity<Map> loginResponse = client.login(username, password);
        context.setLastResponse(loginResponse);
    }

    @When("I login with the registered trainee credentials")
    public void iLoginWithTheRegisteredTraineeCredentials() {
        AuthCredentials creds = context.getCredentials();
        validateCredentials(creds);

        ResponseEntity<Map> loginResponse = client.login(creds.getUsername(), creds.getPassword());
        context.setLastResponse(loginResponse);
        context.setJwtToken(extractJwtToken(loginResponse));
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
        Assertions.assertThat(credentials.getUsername())
                .as("Username should not be empty")
                .isNotEmpty();
        Assertions.assertThat(credentials.getPassword())
                .as("Password should not be empty")
                .isNotEmpty();
    }

    private String extractJwtToken(ResponseEntity<Map> response) {
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        Assertions.assertThat(cookies)
                .as("Set-Cookie headers should be present")
                .isNotEmpty();

        return cookies.stream()
                .filter(cookie -> cookie.startsWith("JWT="))
                .findFirst()
                .map(cookie -> cookie.split(";")[0].substring("JWT=".length()))
                .orElseThrow(() -> new AssertionError("JWT cookie not found"));
    }
}