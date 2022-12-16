package com.systelab.seed.features.patient.allergy.service.command;

import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.repository.PatientAllergyRepository;
import com.systelab.seed.features.patient.allergy.service.AllergyForPatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PatientAllergyUpdateCommandService {
    private final PatientAllergyRepository patientAllergyRepository;

    public PatientAllergy updateAllergyToPatient(UUID patientId, UUID allergyId, @Valid PatientAllergy patientAllergy) {
        return this.patientAllergyRepository.findByPatientIdAndAllergyId(patientId, allergyId).map(existing -> {
            existing.setNote(patientAllergy.getNote());
            existing.setAssertedDate(patientAllergy.getAssertedDate());
            existing.setLastOccurrence(patientAllergy.getLastOccurrence());
            return this.patientAllergyRepository.saveAndFlush(existing);
        }).orElseThrow(() -> new AllergyForPatientNotFoundException(patientId, allergyId));
    }
}
