package com.systelab.seed.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component

public class PatientMaintenanceServiceHealthIndicator implements HealthIndicator {
    private final String lastUpdateTag = "last-update";

    private boolean working = true;
    private LocalDateTime lastExecution = LocalDateTime.MIN;

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

    public void setLastExecution(LocalDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }
}