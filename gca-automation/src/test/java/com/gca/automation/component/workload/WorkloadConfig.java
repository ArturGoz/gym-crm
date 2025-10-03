package com.gca.automation.component.workload;

import com.gca.automation.AutomationApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static java.lang.String.format;

@CucumberContextConfiguration
@SpringBootTest(
        classes = AutomationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
public class WorkloadConfig {

    private static final String ACTIVEMQ_IMAGE = "apache/activemq-classic:latest";
    private static final String MONGODB_IMAGE = "mongo:7.0.5";
    private static final String WORKLOAD_IMAGE = "workload-service:latest";

    private static final Network network = Network.newNetwork();

    @Container
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGODB_IMAGE))
            .withNetwork(network)
            .withNetworkAliases("mongodb");

    @Container
    private final static GenericContainer<?> activemqContainer = new GenericContainer<>(DockerImageName.parse(ACTIVEMQ_IMAGE))
            .withNetwork(network)
            .withNetworkAliases("activemq")
            .withExposedPorts(61616, 8161);

    @Container
    private final static GenericContainer<?> workloadService = new GenericContainer<>(DockerImageName.parse(WORKLOAD_IMAGE))
            .withNetwork(network)
            .withExposedPorts(8081)
            .dependsOn(mongoDBContainer, activemqContainer)
            .withEnv("SPRING_PROFILES_ACTIVE", "automation-test")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("workload")));

    static {
        mongoDBContainer.start();
        activemqContainer.start();
        workloadService.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("testdb"));

        registry.add("spring.activemq.broker-url", () ->
                format("tcp://%s:%d",
                        activemqContainer.getHost(),
                        activemqContainer.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "admin");
        registry.add("spring.activemq.password", () -> "admin");

        registry.add("jms.queue.trainer-workload", () -> "trainer.workload.queue");
        registry.add("jms.request-timeout-ms", () -> 5000);
    }
}