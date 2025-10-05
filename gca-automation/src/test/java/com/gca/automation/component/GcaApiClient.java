package com.gca.automation.component;

import com.gca.automation.dto.LoginRequest;
import com.gca.automation.dto.TraineeRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GcaApiClient {

    private static final String BASE_URL = "/api/v1/";
    private static final String LOCALHOST_URL = "http://localhost:";

    private final int port;
    private final TestRestTemplate restTemplate;

    @Autowired
    public GcaApiClient(TestRestTemplate restTemplate,
                        @Value("${test.port:0}") int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    public ResponseEntity<String> registerTraineeRaw(TraineeRegistrationRequest request) {
        String url = buildUrl("trainees/register");
        HttpEntity<TraineeRegistrationRequest> httpRequest = createJsonRequest(request);

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);
    }

    public ResponseEntity<Map> registerTrainee(TraineeRegistrationRequest request) {
        String url = buildUrl("trainees/register");
        HttpEntity<TraineeRegistrationRequest> httpRequest = createJsonRequest(request);

        return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
    }

    public ResponseEntity<Map> login(String username, String password) {
        String url = buildUrl("auth/login");
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpEntity<LoginRequest> httpRequest = createJsonRequest(loginRequest);

        try {
            return restTemplate.exchange(url, HttpMethod.POST, httpRequest, Map.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private String buildUrl(String endpoint) {
        return String.format("%s%d%s%s", LOCALHOST_URL, port, BASE_URL, endpoint);
    }

    private <T> HttpEntity<T> createJsonRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}
