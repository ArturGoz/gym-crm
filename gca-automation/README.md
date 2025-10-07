# GCA Automation

This project requires a **Docker environment** to be available.

## Preparing Docker Images

Before running tests, you need to build the required Docker images.  
Navigate to the project path:

```bash
cd ./gym-crm-application-microservices
```

```bash
docker build -t discovery-service:latest -f discovery-service/Dockerfile .
docker build -t workload-service:latest -f workload-service/Dockerfile .
docker build -t gca-core-service:latest -f gca-core-service/Dockerfile .
docker build -t api-gateway-service:latest -f api-gateway-service/Dockerfile .
```
Note: ActiveMQ, MongoDB, and PostgreSQL images will be automatically downloaded by Docker.

## Running Tests
Navigate to the project path:

```bash
cd ./gca-automation
```

Run All Tests

```bash
mvn test "-Dcucumber.plugin=summary"
```

Run Specific Test Scenarios
To run one or more specific scenarios, use the -Dcucumber.filter.name option.
Multiple scenarios can be separated by |. Specify the glue package with -Dcucumber.glue.

Examples : 
```bash
# Run multiple scenarios
mvn test "-Dcucumber.filter.name=Successful trainee registration|Failed trainee registration with invalid data" "-Dcucumber.glue=com.gca.automation.component.gca"

# Run a single scenario
mvn test "-Dcucumber.filter.name=Successful trainee registration" "-Dcucumber.glue=com.gca.automation.component.gca"
```
Notes
- -Dcucumber.filter.name=NAME_OF_SCENARIO — specifies which scenario(s) to run.
- -Dcucumber.glue=PATH_TO_PACKAGE_WITH_CONFIG_OF_TEST — points to the package containing step definitions and configuration.
