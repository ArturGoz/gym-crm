package com.gca.automation.integration.steps;

import com.gca.automation.integration.WorkloadSender;
import com.gca.automation.dto.ActionType;
import com.gca.automation.dto.TrainerWorkloadDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.gca.automation.dto.ActionType.ADD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class WorkloadServiceSteps {

    private static final String DEFAULT_FIRST_NAME = "Ronnie";
    private static final String DEFAULT_LAST_NAME = "Coleman";
    private static final boolean DEFAULT_ACTIVE = true;
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 1, 1);
    private static final Integer DEFAULT_DURATION = 11;

    private static final String COLLECTION_NAME = "trainer_workloads";

    @Autowired
    private WorkloadSender sender;

    @Autowired
    private MongoTemplate template;

    @Before
    public void cleanBeforeScenario() {
        template.getCollection(COLLECTION_NAME).deleteMany(new Document());
    }

    @Given("a trainer exists with username {string}")
    public void a_trainer_exists(String username) {
        TrainerWorkloadDTO dto = buildTrainerDTO(username, DEFAULT_DURATION, DEFAULT_DATE, ADD);
        sender.processTrainerWorkloadRequest(dto);
        awaitTrainer(username);
    }

    @When("I add a workload of {int} minutes for {string} in month {int} of {int} year")
    public void i_add_workload(int minutes, String username, int month, int year) {
        TrainerWorkloadDTO dto = buildTrainerDTO(username, minutes, LocalDate.of(year, month, 15), ADD);
        sender.processTrainerWorkloadRequest(dto);
        awaitTrainer(username);
    }

    @When("I remove a workload of {int} minutes for {string} in month {int} of {int} year")
    public void i_remove_workload(int minutes, String username, int month, int year) {
        TrainerWorkloadDTO dto = buildTrainerDTO(username, minutes, LocalDate.of(year, month, 15), ActionType.DELETE);
        sender.processTrainerWorkloadRequest(dto);
        awaitTrainer(username);
    }

    @When("I add workload")
    public void i_add_workload(DataTable table) {
        table.asMaps().forEach(row -> {
            String username = row.get("trainerUsername");
            int minutes = Integer.parseInt(row.get("minutes"));
            int month = Integer.parseInt(row.get("month"));
            int year = Integer.parseInt(row.get("year"));

            TrainerWorkloadDTO dto = buildTrainerDTO(username, minutes,
                    LocalDate.of(year, month, 15), ActionType.ADD);

            sender.processTrainerWorkloadRequest(dto);
            awaitTrainer(username);
        });
    }

    @Then("the trainer summary should contain")
    public void the_trainer_summary_should_contain(DataTable table) {
        table.asMaps().forEach(row -> {
            String username = row.get("trainerUsername");
            int expectedMinutes = Integer.parseInt(row.get("minutes"));
            int month = Integer.parseInt(row.get("month"));
            int year = Integer.parseInt(row.get("year"));

            Document doc = findTrainerDoc(username);
            assertThat(doc).isNotNull();

            Document yearDoc = findYearDoc(doc, year);
            Document monthDoc = findMonthDoc(yearDoc, month);

            assertThat(monthDoc.getLong("trainingSummaryDuration"))
                    .as("Expected %s minutes for trainer %s in %s/%s",
                            expectedMinutes, username, month, year)
                    .isEqualTo(expectedMinutes);
        });
    }

    @Then("the trainer document for {string} should exist in database")
    public void the_trainer_document_should_exist(String username) {
        Document doc = findTrainerDoc(username);
        assertThat(doc)
                .describedAs("Trainer %s should exist in DB", username)
                .isNotNull();
    }

    @Then("the trainer summary for {string} should contain {int} minutes for month {int} of {int} year")
    public void the_trainer_summary_should_contain(String username, int expectedMinutes, int month, int year) {
        Document doc = findTrainerDoc(username);
        assertThat(doc).isNotNull();

        Document yearDoc = findYearDoc(doc, year);
        Document monthDoc = findMonthDoc(yearDoc, month);

        assertThat(monthDoc.getLong("trainingSummaryDuration"))
                .describedAs("Expected %s minutes for trainer %s in %s/%s",
                        expectedMinutes, username, month, year)
                .isEqualTo(expectedMinutes);
    }

    @Then("no workload should exist for {string} in month {int} of {int} year")
    public void no_workload_should_exist(String username, int month, int year) {
        Document trainerDoc = findTrainerDoc(username);
        assertThat(trainerDoc)
                .as("Trainer document for %s should exist", username)
                .isNotNull();

        boolean monthExists = Optional.ofNullable(findYearDocOrNull(trainerDoc, year))
                .map(y -> (List<Document>) y.get("months"))
                .stream()
                .flatMap(List::stream)
                .anyMatch(m -> m.getInteger("month").equals(month));

        assertThat(monthExists)
                .as("Month %s of %s should not exist for trainer %s", month, year, username)
                .isFalse();
    }

    private TrainerWorkloadDTO buildTrainerDTO(String username, int minutes, LocalDate date, ActionType actionType) {
        return TrainerWorkloadDTO.builder()
                .trainerUsername(username)
                .trainerFirstName(DEFAULT_FIRST_NAME)
                .trainerLastName(DEFAULT_LAST_NAME)
                .isActive(DEFAULT_ACTIVE)
                .trainingDate(date)
                .trainingDuration(minutes)
                .actionType(actionType)
                .build();
    }

    private Document findTrainerDoc(String username) {
        return template.getCollection(COLLECTION_NAME)
                .find(new Document("username", username))
                .first();
    }

    private Document findYearDoc(Document trainerDoc, int year) {
        return ((List<Document>) trainerDoc.get("years")).stream()
                .filter(y -> y.getInteger("year").equals(year))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Year not found: " + year));
    }

    private Document findYearDocOrNull(Document trainerDoc, int year) {
        return ((List<Document>) trainerDoc.get("years")).stream()
                .filter(y -> y.getInteger("year").equals(year))
                .findFirst()
                .orElse(null);
    }

    private Document findMonthDoc(Document yearDoc, int month) {
        return ((List<Document>) yearDoc.get("months")).stream()
                .filter(m -> m.getInteger("month").equals(month))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Month not found: " + month));
    }

    private void awaitTrainer(String username) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> findTrainerDoc(username) != null);
    }
}