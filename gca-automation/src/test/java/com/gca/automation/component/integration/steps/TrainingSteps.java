package com.gca.automation.component.integration.steps;

import com.gca.automation.component.GcaApiClient;
import com.gca.automation.component.TestContext;
import com.gca.automation.component.integration.IntegrationConfig;
import com.gca.automation.dto.TrainingRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = IntegrationConfig.class)
@Transactional
public class TrainingSteps {
    @Autowired
    private GcaApiClient client;

    @Autowired
    private TestContext context;

    @When("I create a new training with the following details:")
    public void iCreateNewTraining(DataTable dataTable) {
        Map<String, String> trainingData = dataTable.asMaps().get(0);
        TrainingRequest request = buildTrainingRequest(trainingData);

        ResponseEntity<Map> response = client.createTraining(request, context.getJwtToken());
        context.setLastResponse(response);
    }

    private TrainingRequest buildTrainingRequest(Map<String, String> trainingData) {
        return TrainingRequest.builder()
                .traineeUsername(trainingData.get("traineeUsername"))
                .trainerUsername(trainingData.get("trainerUsername"))
                .trainingName(trainingData.get("trainingName"))
                .trainingDate(trainingData.get("trainingDate"))
                .duration(Integer.parseInt(trainingData.get("duration")))
                .build();
    }
}
