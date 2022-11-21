package com.systelab.seed.features.patient.service.command;

import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientCreationCommandService {

    private final PatientRepository patientRepository;

    public Patient createPatient(Patient p) {
        return this.patientRepository.save(p);
    }

}
