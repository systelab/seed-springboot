package com.systelab.seed.features.patient.allergy.service.command;

import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.repository.PatientAllergyRepository;
import com.systelab.seed.features.patient.allergy.service.AllergyForPatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PatientAllergyDeleteCommandService {
    private final PatientAllergyRepository patientAllergyRepository;

    public PatientAllergy removeAllergyFromPatient(UUID patientId, UUID allergyId) {
        return this.patientAllergyRepository.findByPatientIdAndAllergyId(patientId, allergyId).map(existing -> {
            this.patientAllergyRepository.delete(existing);
            return existing;
        }).orElseThrow(() -> new AllergyForPatientNotFoundException(patientId, allergyId));
    }
}
