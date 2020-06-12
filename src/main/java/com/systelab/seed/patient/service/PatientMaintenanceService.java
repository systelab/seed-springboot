package com.systelab.seed.patient.service;


import com.systelab.seed.infrastructure.health.PatientMaintenanceServiceHealthIndicator;
import com.systelab.seed.patient.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;


@Service
public class PatientMaintenanceService {

    private Logger logger = LoggerFactory.getLogger(PatientMaintenanceService.class);

    private final PatientRepository patientRepository;

    private final PatientMaintenanceServiceHealthIndicator healthIndicator;

    @Autowired
    public PatientMaintenanceService(PatientRepository patientRepository, PatientMaintenanceServiceHealthIndicator healthIndicator) {
        this.patientRepository = patientRepository;
        this.healthIndicator = healthIndicator;
    }

    @Scheduled(cron = "${patient.maintenance.cron.expression}")
    public void schedulePurgeOlderRecordsTask() {
        try {
            this.patientRepository.setActiveForUpdatedBefore(ZonedDateTime.now().minusYears(1));
            logger.info("Patients DB purged!");
            healthIndicator.setWorking(true);
            healthIndicator.setLastExecution(ZonedDateTime.now());
        } catch (Exception ex) {
            logger.error("Patients DB not purged!", ex);
            healthIndicator.setWorking(false);
        }
    }
}