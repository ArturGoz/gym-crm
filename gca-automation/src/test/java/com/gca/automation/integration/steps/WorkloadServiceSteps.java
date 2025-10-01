package com.gca.automation.integration.steps;

import com.gca.openapi.model.TrainerWorkloadRequest;
import com.gca.workloadservice.exception.EntityNotFoundException;
import com.gca.workloadservice.model.MonthWorkload;
import com.gca.workloadservice.model.TrainerWorkload;
import com.gca.workloadservice.model.YearWorkload;
import com.gca.workloadservice.repository.TrainerWorkloadRepository;
import com.gca.workloadservice.service.TrainerWorkloadService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static com.gca.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.ADD;
import static com.gca.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.DELETE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class WorkloadServiceSteps {

    private static final String DEFAULT_FIRST_NAME = "Ronnie";
    private static final String DEFAULT_LAST_NAME = "Coleman";
    private static final boolean DEFAULT_ACTIVE = true;
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2025, 1, 1);
    private static final Integer DEFAULT_DURATION = 11;

    @Autowired
    private TrainerWorkloadRepository repository;

    @Autowired
    private TrainerWorkloadService service;

    private Exception capturedException;

    @Given("a trainer exists with username {string}")
    public void a_trainer_exists(String username) {
        service.addTrainingWorkload(buildTrainerRequestWithUsername(username));
    }

    @When("I add a workload of {int} minutes for {string} in month {int} of {int}")
    public void i_add_workload(int minutes, String username, int month, int year) {
        buildTrainerRequest(username, minutes, month, year, ADD);
    }

    @When("I remove a workload of {int} minutes for {string} in month {int} of {int}")
    public void i_remove_workload(int minutes, String username, int month, int year) {
        buildTrainerRequest(username, minutes, month, year, DELETE);
    }

    @When("I try to add workload of {int} minutes for {string} in month {int} of {int}")
    public void i_try_to_add_invalid_workload(int minutes, String username, int month, int year) {
        try {
            buildTrainerRequest(username, minutes, month, year, ADD);
        } catch (ConstraintViolationException e) {
            this.capturedException = e;
        }
    }

    @When("I request workload summary for {string} in month {int} of {int}")
    public void i_request_workload_summary(String username, int month, int year) {
        try {
            service.getTrainerWorkloadDurationSummary(username, year, month);
        } catch (EntityNotFoundException e) {
            this.capturedException = e;
        }
    }

    @Then("the trainer summary for {string} should contain {int} minutes for month {int} of {int}")
    public void the_trainer_summary_should_contain(String username, int expectedMinutes, int month, int year) {
        MonthWorkload monthWorkload = findMonthWorkload(username, year, month);

        assertThat(monthWorkload.getTrainingSummaryDuration()).isEqualTo(expectedMinutes);
    }

    @Then("a validation exception should be thrown")
    public void a_validation_exception_should_be_thrown() {
        assertThat(capturedException)
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Then("a not found exception should be thrown")
    public void a_not_found_exception_should_be_thrown() {
        assertThat(capturedException)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trainer not found");
    }

    private void buildTrainerRequest(String username, int minutes, int month, int year,
                                     TrainerWorkloadRequest.ActionTypeEnum actionType) {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName(DEFAULT_FIRST_NAME);
        request.setTrainerLastName(DEFAULT_LAST_NAME);
        request.setIsActive(DEFAULT_ACTIVE);
        request.setTrainingDate(LocalDate.of(year, month, 15));
        request.setTrainingDuration(minutes);
        request.setActionType(actionType);

        service.addTrainingWorkload(request);
    }

    private TrainerWorkloadRequest buildTrainerRequestWithUsername(String username) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName(DEFAULT_FIRST_NAME);
        request.setTrainerLastName(DEFAULT_LAST_NAME);
        request.setIsActive(DEFAULT_ACTIVE);
        request.setTrainingDate(DEFAULT_DATE);
        request.setTrainingDuration(DEFAULT_DURATION);
        request.setActionType(ADD);

        return request;
    }

    private MonthWorkload findMonthWorkload(String username, int year, int month) {
        TrainerWorkload trainer = findTrainer(username);
        YearWorkload targetYearly = findYearWorkload(trainer, year);

        return targetYearly.getMonths().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Month not found: " + month));
    }

    private TrainerWorkload findTrainer(String username) {
        return repository.findTrainerWorkloadsByUsername(username)
                .orElseThrow(() -> new AssertionError(
                        "Trainer workload not found for username: " + username));
    }

    private YearWorkload findYearWorkload(TrainerWorkload trainer, int year) {
        return trainer.getYears().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Year not found: " + year));
    }
}
