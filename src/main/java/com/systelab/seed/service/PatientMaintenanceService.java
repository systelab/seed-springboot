package com.systelab.seed.service;

import com.systelab.seed.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class PatientMaintenanceService {

    Logger logger = LoggerFactory.getLogger(PatientMaintenanceService.class);

    @Autowired
    private PatientRepository patientRepository;

    @Scheduled(cron = "${patient.maintenance.cron.expression}")
    public void schedulePurgeOlderRecordsTask() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        this.patientRepository.setActiveForUpdatedBefore(cal.getTime());
        logger.info("Patients DB purged!");
    }
}