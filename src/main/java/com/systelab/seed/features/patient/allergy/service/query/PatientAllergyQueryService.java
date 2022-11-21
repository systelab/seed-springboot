package com.systelab.seed.features.patient.allergy.service.query;

import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.service.query.PatientQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PatientAllergyQueryService {

    private final PatientQueryService patientQueryService;

    public Set<PatientAllergy> getAllergiesFromPatient(UUID patientId) {
        Patient patient = this.patientQueryService.getPatient(patientId);
        return patient.getAllergies();
    }
}
