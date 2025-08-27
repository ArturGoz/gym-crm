package com.gca.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long THRESHOLD = 500L * 1024 * 1024;

    @Override
    public Health health() {
        long freeSpace = getFreeDiskSpace();

        if (freeSpace > THRESHOLD) {
            return Health.up().withDetail("Disk Space", formatSize(freeSpace)).build();
        }

        return Health.down()
                .withDetail("Disk Space", formatSize(freeSpace))
                .withDetail("Error", "Low disk space")
                .build();
    }

    protected long getFreeDiskSpace() {
        File root = new File("/");

        return root.getFreeSpace();
    }

    private String formatSize(long bytes) {
        long mb = bytes / (1024 * 1024);

        return mb + " MB";
    }
}
