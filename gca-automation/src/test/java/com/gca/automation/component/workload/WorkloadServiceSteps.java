package com.gca.automation.component.workload;

import com.gca.automation.dto.ActionType;
import com.gca.automation.dto.TrainerWorkloadDTO;
import com.gca.automation.integration.WorkloadSender;
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
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gca.automation.dto.ActionType.ADD;
import static com.gca.automation.dto.ActionType.DELETE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ContextConfiguration(classes = WorkloadServiceSteps.class)
public class WorkloadServiceSteps {
    private static final String DEFAULT_FIRST_NAME = "Ronnie";
    private static final String DEFAULT_LAST_NAME = "Coleman";
    private static final boolean DEFAULT_ACTIVE = true;
    private static final int DEFAULT_DAY = 15;
    private static final Integer DEFAULT_DURATION = 11;
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 1, 1);
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
        processWorkload(username, DEFAULT_DURATION, DEFAULT_DATE, ADD);
    }

    @When("I add a workload of {int} minutes for {string} in month {int} of {int} year")
    public void i_add_workload(int minutes, String username, int month, int year) {
        processWorkload(username, minutes, LocalDate.of(year, month, DEFAULT_DAY), ADD);
    }

    @When("I remove a workload of {int} minutes for {string} in month {int} of {int} year")
    public void i_remove_workload(int minutes, String username, int month, int year) {
        processWorkload(username, minutes, LocalDate.of(year, month, DEFAULT_DAY), DELETE);
    }

    @When("I add workload")
    public void i_add_workload(DataTable table) {
        parseWorkloadTable(table).stream()
                .map(this::buildAddTrainerDTOFromRow)
                .forEach(this::processAndAwait);
    }

    @Then("the trainer summary should contain")
    public void the_trainer_summary_should_contain(DataTable table) {
        parseWorkloadTable(table).forEach(this::assertTrainingSummaryDuration);
    }

    @Then("the trainer document for {string} should exist in database")
    public void the_trainer_document_should_exist(String username) {
        assertThat(findTrainerDoc(username))
                .describedAs("Trainer %s should exist in DB", username)
                .isNotNull();
    }

    @Then("the trainer summary for {string} should contain {int} minutes for month {int} of {int} year")
    public void the_trainer_summary_should_contain(String username, int expectedMinutes, int month, int year) {
        assertTrainingSummaryDuration(new WorkloadRow(username, expectedMinutes, month, year));
    }

    @Then("no workload should exist for {string} in month {int} of {int} year")
    public void no_workload_should_exist(String username, int month, int year) {
        Document trainerDoc = findTrainerDoc(username);
        boolean monthExists = Optional.ofNullable(findYearDocOrNull(trainerDoc, year))
                .map(y -> (List<Document>) y.get("months"))
                .stream()
                .flatMap(List::stream)
                .anyMatch(m -> m.getInteger("month").equals(month));

        assertThat(trainerDoc).isNotNull();
        assertThat(monthExists)
                .as("Month %s of %s should not exist for trainer %s", month, year, username)
                .isFalse();
    }

    private void processWorkload(String username, int minutes, LocalDate date, ActionType actionType) {
        TrainerWorkloadDTO dto = buildTrainerDTO(username, minutes, date, actionType);
        processAndAwait(dto);
    }

    private void processAndAwait(TrainerWorkloadDTO dto) {
        sender.processTrainerWorkloadRequest(dto);
        awaitTrainer(dto.getTrainerUsername());
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

    private TrainerWorkloadDTO buildAddTrainerDTOFromRow(WorkloadRow row) {
        return buildTrainerDTO(
                row.username(),
                row.minutes(),
                LocalDate.of(row.year(), row.month(), DEFAULT_DAY),
                ADD
        );
    }

    private Document findTrainerDoc(String username) {
        return template.getCollection(COLLECTION_NAME)
                .find(new Document("username", username))
                .first();
    }

    private Document findYearDocOrNull(Document trainerDoc, int year) {
        return getYears(trainerDoc).stream()
                .filter(y -> y.getInteger("year").equals(year))
                .findFirst()
                .orElse(null);
    }

    private Document findYearDoc(Document trainerDoc, int year) {
        return Optional.ofNullable(findYearDocOrNull(trainerDoc, year))
                .orElseThrow(() -> new AssertionError("Year not found: " + year));
    }

    private Document findMonthDoc(Document yearDoc, int month) {
        return getMonths(yearDoc).stream()
                .filter(m -> m.getInteger("month").equals(month))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Month not found: " + month));
    }

    private List<Document> getYears(Document trainerDoc) {
        return (List<Document>) trainerDoc.get("years");
    }

    private List<Document> getMonths(Document yearDoc) {
        return (List<Document>) yearDoc.get("months");
    }

    private void awaitTrainer(String username) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> findTrainerDoc(username) != null);
    }

    private List<WorkloadRow> parseWorkloadTable(DataTable table) {
        return table.asMaps().stream()
                .map(this::mapToWorkloadRow)
                .toList();
    }

    private WorkloadRow mapToWorkloadRow(Map<String, String> row) {
        return new WorkloadRow(
                row.get("trainerUsername"),
                Integer.parseInt(row.get("minutes")),
                Integer.parseInt(row.get("month")),
                Integer.parseInt(row.get("year"))
        );
    }

    private void assertTrainingSummaryDuration(WorkloadRow row) {
        Document trainerDoc = findTrainerDoc(row.username());
        Document yearDoc = findYearDoc(trainerDoc, row.year());
        Document monthDoc = findMonthDoc(yearDoc, row.month());

        assertThat(trainerDoc).isNotNull();
        assertThat(monthDoc.getLong("trainingSummaryDuration"))
                .as("Expected %s minutes for trainer %s in %s/%s",
                        row.minutes(), row.username(), row.month(), row.year())
                .isEqualTo(row.minutes());
    }

    private record WorkloadRow(String username, int minutes, int month, int year) {
    }
}