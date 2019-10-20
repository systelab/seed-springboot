package com.systelab.seed.config.health;

import com.systelab.seed.service.PatientMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PatientMaintenanceServiceHealthIndicator implements HealthIndicator {
    private final String lastUpdateTag = "last-update";

    private final PatientMaintenanceService service;

    @Autowired
    public PatientMaintenanceServiceHealthIndicator(PatientMaintenanceService service) {
        this.service = service;
    }

    @Override
    public Health health() {
        if (!service.isWorking()) {
            return Health.down()
                    .withDetail(lastUpdateTag, service.lastExecution().orElse(LocalDateTime.MIN))
                    .build();
        }
        return Health.up()
                .withDetail(lastUpdateTag, service.lastExecution().orElse(LocalDateTime.MIN))
                .build();
    }
}