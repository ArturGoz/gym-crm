package com.gca.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DiskSpaceHealthIndicatorTest {

    @Spy
    private DiskSpaceHealthIndicator indicator;

    @Test
    void shouldReturnUpWhenDiskSpaceIsSufficient() {
        doReturn(1024L * 1024 * 1024).when(indicator).getFreeDiskSpace();

        Health actual = indicator.health();

        assertEquals("UP", actual.getStatus().getCode());
        assertEquals("1024 MB", actual.getDetails().get("Disk Space"));
    }

    @Test
    void shouldReturnDownWhenDiskSpaceIsLow() {
        doReturn(100L * 1024 * 1024).when(indicator).getFreeDiskSpace();

        Health actual = indicator.health();

        assertEquals("DOWN", actual.getStatus().getCode());
        assertEquals("100 MB", actual.getDetails().get("Disk Space"));
        assertEquals("Low disk space", actual.getDetails().get("Error"));
    }
}