package com.gca.automation.component.integration;

import com.gca.automation.AutomationApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@CucumberContextConfiguration
@SpringBootTest(
        classes = AutomationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
public class IntegrationConfig {

    private static final String HEALTH_URL = "/actuator/health";
    private static final File DOCKER_COMPOSE_FILE =
            new File("../docker-compose-test.yml");

    @Container
    public static DockerComposeContainer<?> composeContainer =
            new DockerComposeContainer<>(DOCKER_COMPOSE_FILE)
                    .withExposedService("discovery-service", 8761)
                    .withExposedService("api-gateway-service", 8765)
                    .withExposedService("workload-service", 8081)
                    .withExposedService("gca-core-service", 8080)
                    .withExposedService("postgres", 5432)
                    .withExposedService("mongo", 27017)
                    .withExposedService("activemq", 61616)
                    .withExposedService("activemq", 8161)
                    .waitingFor("workload-service", Wait.forHttp(HEALTH_URL)
                            .forPort(8081)
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofSeconds(120)))
                    .waitingFor("gca-core-service", Wait.forHttp(HEALTH_URL)
                            .forPort(8080)
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofSeconds(120)))
                    .waitingFor("api-gateway-service", Wait.forHttp(HEALTH_URL)
                            .forPort(8765)
                            .forStatusCode(200)
                            .withStartupTimeout(Duration.ofMinutes(3)));

    static {
        composeContainer.start();
        waitForAllServicesSetUp();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("test.port", IntegrationConfig::getGatewayPort);
    }

    public static int getGatewayPort() {
        return composeContainer.getServicePort("api-gateway-service", 8765);
    }

    private static void waitForAllServicesSetUp() {
        try {
            Thread.sleep(20000);
        } catch (Exception ignored) {
        }
    }
}
