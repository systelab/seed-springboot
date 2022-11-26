package com.systelab.seed.core.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.*;

@Component

public class PatientMaintenanceServiceHealthIndicator implements HealthIndicator {
    private static final String LAST_UPDATE_TAG = "last-update";

    private boolean working = true;
    private ZonedDateTime lastExecution = Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneOffset.UTC);

    @Override
    public Health health() {
        if (working) {
            return Health.up()
                    .withDetail(LAST_UPDATE_TAG, lastExecution)
                    .build();
        }
        return Health.down()
                .withDetail(LAST_UPDATE_TAG, lastExecution)
                .build();
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public void setLastExecution(ZonedDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }
}