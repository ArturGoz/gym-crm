package com.gca.automation.component;

import com.gca.automation.dto.LoginRequest;
import com.gca.automation.dto.TraineeRegistrationRequest;
import com.gca.automation.dto.TrainerRegistrationRequest;
import com.gca.automation.dto.TrainingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GcaApiClient {
    private static final String BASE_URL = "/api/v1/";
    private static final String LOCALHOST_URL = "http://localhost:";

    private final int port;
    private final TestRestTemplate restTemplate;

    public GcaApiClient(TestRestTemplate restTemplate, @Value("${test.port:0}") int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    public ResponseEntity<Map> registerTrainee(TraineeRegistrationRequest request) {
        String url = buildUrl("trainees/register");
        HttpEntity<TraineeRegistrationRequest> httpRequest = createJsonRequest(request);

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
    }

    public ResponseEntity<Map> registerTrainer(TrainerRegistrationRequest request) {
        String url = buildUrl("trainers/register");
        HttpEntity<TrainerRegistrationRequest> httpRequest = createJsonRequest(request);

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
    }

    public ResponseEntity<Map> login(String username, String password) {
        String url = buildUrl("auth/login");
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpEntity<LoginRequest> httpRequest = createJsonRequest(loginRequest);

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
    }

    public ResponseEntity<Map> createTraining(TrainingRequest request, String jwtToken) {
        String url = buildUrl("trainings");
        HttpEntity<TrainingRequest> httpRequest = new HttpEntity<>(request, buildJwtHeadersForPost(jwtToken));

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
    }

    public ResponseEntity<Long> getTrainerWorkload(String trainerUsername, int year, int month, String jwtToken) {
        String url = buildUrl(
                String.format("trainers/workload/%s?year=%d&month=%d", trainerUsername, year, month)
        );
        HttpEntity<Void> httpRequest = new HttpEntity<>(buildJwtHeadersForGet(jwtToken));

        return restTemplate.exchange(url, HttpMethod.GET, httpRequest, Long.class);
    }

    public ResponseEntity<Map> getTraineeProfile(String username, String jwtToken) {
        String url = buildUrl("trainees/" + username);
        HttpEntity<Void> httpRequest = new HttpEntity<>(buildJwtHeadersForPost(jwtToken));

        return restTemplate.exchange(url, HttpMethod.GET, httpRequest, Map.class);
    }

    private String buildUrl(String endpoint) {
        return String.format("%s%d%s%s", LOCALHOST_URL, port, BASE_URL, endpoint);
    }

    private <T> HttpEntity<T> createJsonRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders buildJwtHeaders(String token, boolean isPost) {
        HttpHeaders headers = new HttpHeaders();

        if (isPost) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        } else {
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        }

        if (token != null && !token.isEmpty()) {
            headers.add(HttpHeaders.COOKIE, "JWT=" + token);
        }

        return headers;
    }

    private HttpHeaders buildJwtHeadersForPost(String token) {
        return buildJwtHeaders(token, true);
    }

    private HttpHeaders buildJwtHeadersForGet(String token) {
        return buildJwtHeaders(token, false);
    }

}