package com.systelab.seed.core.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.*;

@Component

public class PatientMaintenanceServiceHealthIndicator implements HealthIndicator {
    private final String lastUpdateTag = "last-update";

    private boolean working = true;
    private ZonedDateTime lastExecution = Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneOffset.UTC);

    @Override
    public Health health() {
        if (working) {
            return Health.up()
                    .withDetail(lastUpdateTag, lastExecution)
                    .build();
        }
        return Health.down()
                .withDetail(lastUpdateTag, lastExecution)
                .build();
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public void setLastExecution(ZonedDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }
}