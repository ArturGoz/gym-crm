package com.gca.automation.component.gca;

import com.gca.automation.AutomationApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
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
public class GcaCoreConfig {

    private static final String ACTIVEMQ_IMAGE = "apache/activemq-classic:latest";
    private static final String POSTGRES_IMAGE = "postgres:14.19-alpine3.21";
    private static final String GCA_CORE_IMAGE = "gca-core-service:latest";

    private static final Network network = Network.newNetwork();

    @Container
    private static final PostgreSQLContainer<?> postgresDBContainer = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
            .withDatabaseName("gca-core-test")
            .withUsername("test")
            .withPassword("test")
            .withNetwork(network)
            .withNetworkAliases("postgres");

    @Container
    private static final GenericContainer<?> activemqContainer = new GenericContainer<>(DockerImageName.parse(ACTIVEMQ_IMAGE))
            .withNetwork(network)
            .withNetworkAliases("activemq")
            .withExposedPorts(61616, 8161)
            .withEnv("ACTIVEMQ_ADMIN_LOGIN", "test")
            .withEnv("ACTIVEMQ_ADMIN_PASSWORD", "test");

    @Container
    private static final GenericContainer<?> gcaCoreServiceContainer = new GenericContainer<>(DockerImageName.parse(GCA_CORE_IMAGE))
            .withNetwork(network)
            .withExposedPorts(8080)
            .dependsOn(postgresDBContainer, activemqContainer)
            .withEnv("SPRING_PROFILES_ACTIVE", "automation-test")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("gca-core")));

    static {
        postgresDBContainer.start();
        activemqContainer.start();
        gcaCoreServiceContainer.start();
    }

    @DynamicPropertySource
    private static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDBContainer::getUsername);
        registry.add("spring.datasource.password", postgresDBContainer::getPassword);

        registry.add("spring.activemq.broker-url", () ->
                format("tcp://%s:%d",
                        activemqContainer.getHost(),
                        activemqContainer.getMappedPort(61616)));
        registry.add("spring.activemq.user", () -> "test");
        registry.add("spring.activemq.password", () -> "test");

        registry.add("jms.queue.trainer-workload", () -> "trainer.workload.queue");
        registry.add("jms.request-timeout-ms", () -> 5000);

        registry.add("test.port", GcaCoreConfig::getGcaCoreMappedPort);
    }

    public static int getGcaCoreMappedPort() {
        return gcaCoreServiceContainer.getMappedPort(8080);
    }
}
