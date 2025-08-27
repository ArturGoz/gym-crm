package com.gca.actuator;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    @Value("${gca.metrics.max-memory-threshold}")
    private long max_memory_threshold_mb;

    @Override
    public Health health() {
        MemoryStats stats = retrieveMemoryStats();
        long usedMemory = stats.heapMemory - stats.freeMemory;

        Health.Builder healthBuilder = usedMemory < max_memory_threshold_mb
                ? Health.up()
                : Health.down();

        return healthBuilder
                .withDetail("usedMemoryMB", usedMemory)
                .withDetail("freeMemoryMB", stats.freeMemory)
                .withDetail("heapMemoryMB", stats.heapMemory)
                .withDetail("maxMemoryMB", stats.maxMemory)
                .withDetail("thresholdMB", max_memory_threshold_mb)
                .build();
    }

    private MemoryStats retrieveMemoryStats() {
        long heapMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);

        return new MemoryStats(heapMemory, freeMemory, maxMemory);
    }

    @AllArgsConstructor
    private static class MemoryStats {
        private long heapMemory;
        private long freeMemory;
        private long maxMemory;
    }
}
