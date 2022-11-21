package com.systelab.seed.features.patient.service.command;

import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.repository.PatientRepository;
import com.systelab.seed.features.patient.service.PatientNotFoundException;
import com.systelab.seed.features.patient.service.query.PatientQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientUpdateCommandService {

    private final PatientRepository patientRepository;
    private final PatientQueryService patientQueryService;

    public Patient updatePatient(UUID id, Patient p) {
        Patient patient=patientQueryService.getPatient(id);
        p.setId(patient.getId());
        return this.patientRepository.save(p);
    }
}
