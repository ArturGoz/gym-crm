package com.gca.automation.integration.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CucumberContextConfiguration
public class StepDefinitions {

    private int a;
    private int b;
    private int result;

    @Given("I have numbers {int} and {int}")
    public void i_have_numbers_and(int num1, int num2) {
        this.a = num1;
        this.b = num2;
    }

    @When("I add them")
    public void i_add_them() {
        this.result = a + b;
    }

    @Then("the result should be {int}")
    public void the_result_should_be(int expected) {
        assertEquals(expected, result);
    }
}
