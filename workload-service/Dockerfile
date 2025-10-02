FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy entire project
COPY . .

# Build only workload-service
RUN mvn clean package -pl workload-service -am -DskipTests=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/workload-service/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]