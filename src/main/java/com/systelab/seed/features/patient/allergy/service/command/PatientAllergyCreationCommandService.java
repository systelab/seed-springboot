package com.systelab.seed.features.patient.allergy.service.command;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.service.query.AllergyQueryService;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.repository.PatientAllergyRepository;
import com.systelab.seed.features.patient.allergy.service.AllergyForPatientAlreadyExistException;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.service.query.PatientQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PatientAllergyCreationCommandService {

    private final PatientQueryService patientQueryService;
    private final PatientAllergyRepository patientAllergyRepository;
    private final AllergyQueryService allergyService;


    public PatientAllergy addAllergyToPatient(UUID id, @Valid PatientAllergy patientAllergy) {
        Patient patient = this.patientQueryService.getPatient(id);
        Allergy allergy = this.allergyService.getAllergy(patientAllergy.getAllergy().getId());

        if (this.patientAllergyRepository.findByPatientIdAndAllergyId(patient.getId(), allergy.getId()).isPresent()) {
            throw new AllergyForPatientAlreadyExistException(patient.getId(), allergy.getId());
        }
        PatientAllergy patientAllergyToAdd = new PatientAllergy(patient, allergy, patientAllergy.getNote());
        patientAllergyToAdd.setAssertedDate(patientAllergy.getAssertedDate());
        patientAllergyToAdd.setLastOccurrence(patientAllergy.getLastOccurrence());
        return this.patientAllergyRepository.save(patientAllergyToAdd);
    }
}
