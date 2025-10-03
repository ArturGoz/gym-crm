package com.gca.automation.component.gca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class GcaCoreServiceSteps {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final int port = GcaCoreConfig.getGcaCoreMappedPort();

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @When("I send a registration request with the following data")
    public void i_send_registration_request(Map<String, String> data) throws Exception {
        response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/trainees/register",
                data,
                String.class
        );
    }

    @Then("the trainee should be successfully registered")
    public void trainee_should_be_registered() throws Exception {
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        JsonNode json = objectMapper.readTree(response.getBody());
        assertThat(json.get("username").asText()).isNotEmpty();
        assertThat(json.get("password").asText()).isNotEmpty();
    }

    @Then("an error response should be returned")
    public void error_response_should_be_returned() throws Exception {
        assertThat(response.getStatusCode().value()).isEqualTo(400);

        JsonNode json = objectMapper.readTree(response.getBody());
        assertThat(json.get("errorCode").asInt()).isGreaterThan(0);
        assertThat(json.get("errorMessage").asText()).isNotEmpty();
    }
}
