package com.gca.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class MemoryHealthIndicatorTest {
    private static final long MAX_MEMORY_THRESHOLD_MB = 2000L;

    private static final String USED_MEMORY_DETAILS = "usedMemoryMB";
    private static final String FREE_MEMORY_DETAILS = "freeMemoryMB";
    private static final String HEAP_MEMORY_DETAILS = "heapMemoryMB";
    private static final String MAX_MEMORY_DETAILS = "maxMemoryMB";
    private static final String THRESHOLD_MEMORY_DETAILS = "thresholdMB";

    private MemoryHealthIndicator indicator;

    @BeforeEach
    void init() {
        indicator = new MemoryHealthIndicator();
        ReflectionTestUtils.setField(indicator, "max_memory_threshold_mb", MAX_MEMORY_THRESHOLD_MB);
    }

    @Test
    void shouldReturnUpWhenMemoryUsageIsBelowThreshold() {
        Health actual = indicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.UP);
        Long heapMemory = (Long) actual.getDetails().get(HEAP_MEMORY_DETAILS);
        Long freeMemory = (Long) actual.getDetails().get(FREE_MEMORY_DETAILS);
        assertThat(heapMemory).isPositive();
        assertThat(freeMemory).isPositive();

        assertThat((Long) actual.getDetails().get(USED_MEMORY_DETAILS)).isEqualTo(heapMemory - freeMemory);
        assertThat((Long) actual.getDetails().get(FREE_MEMORY_DETAILS)).isPositive();
        assertThat((Long) actual.getDetails().get(MAX_MEMORY_DETAILS)).isPositive();
        assertThat((Long) actual.getDetails().get(THRESHOLD_MEMORY_DETAILS)).isPositive();
    }

    @Test
    void shouldReturnDownWhenMemoryUsageExceedsThreshold() {
        ReflectionTestUtils.setField(indicator, "max_memory_threshold_mb", 0L);

        Health actual = indicator.health();

        assertThat(actual.getStatus()).isEqualTo(Status.DOWN);
        Long heapMemory = (Long) actual.getDetails().get(HEAP_MEMORY_DETAILS);
        Long freeMemory = (Long) actual.getDetails().get(FREE_MEMORY_DETAILS);
        assertThat(heapMemory).isPositive();
        assertThat(freeMemory).isPositive();

        assertThat((Long) actual.getDetails().get(USED_MEMORY_DETAILS)).isEqualTo(heapMemory - freeMemory);
        assertThat((Long) actual.getDetails().get(FREE_MEMORY_DETAILS)).isPositive();
        assertThat((Long) actual.getDetails().get(MAX_MEMORY_DETAILS)).isPositive();
        assertThat((Long) actual.getDetails().get(THRESHOLD_MEMORY_DETAILS)).isZero();
    }
}