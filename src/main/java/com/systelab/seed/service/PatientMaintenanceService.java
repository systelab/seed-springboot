package com.systelab.seed.service;

import com.systelab.seed.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatientMaintenanceService {

    private Logger logger = LoggerFactory.getLogger(PatientMaintenanceService.class);

    private final PatientRepository patientRepository;

    @Autowired
    public PatientMaintenanceService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Scheduled(cron = "${patient.maintenance.cron.expression}")
    public void schedulePurgeOlderRecordsTask() {
        this.patientRepository.setActiveForUpdatedBefore(LocalDateTime.now().minusYears(1));
        logger.info("Patients DB purged!");
    }
}