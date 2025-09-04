package com.gca.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GatewayRoutesConfig {

    public static final String BASE_PATH = "/api/v1";
    public static final String LB_GCA_CORE_SERVICE = "lb://GCA-CORE-SERVICE";
    public static final String LB_WORKLOAD_SERVICE = "lb://WORKLOAD-SERVICE";

    public static final List<String> gcaCorePaths = List.of(
            BASE_PATH + "/trainees/**",
            BASE_PATH + "/trainers/**",
            BASE_PATH + "/trainings/**",
            BASE_PATH + "/auth/**"
    );

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("workload-service", r -> r
                        .path(BASE_PATH + "/trainers/workload/**")
                        .uri(LB_WORKLOAD_SERVICE))

                .route("gca-core-service", r -> r
                        .path(gcaCorePaths.toArray(new String[0]))
                        .uri(LB_GCA_CORE_SERVICE))
                .build();
    }
}
