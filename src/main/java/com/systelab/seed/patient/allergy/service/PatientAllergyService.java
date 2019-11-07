package com.systelab.seed.patient.allergy.service;

import com.systelab.seed.allergy.model.Allergy;
import com.systelab.seed.patient.allergy.service.AllergyForPatientAlreadyExistException;
import com.systelab.seed.patient.allergy.service.AllergyForPatientNotFoundException;
import com.systelab.seed.patient.model.Patient;
import com.systelab.seed.patient.allergy.model.PatientAllergy;
import com.systelab.seed.patient.allergy.repository.PatientAllergyRepository;
import com.systelab.seed.patient.service.PatientService;
import com.systelab.seed.service.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientAllergyService {

    private final PatientService patientService;
    private final PatientAllergyRepository patientAllergyRepository;
    private final AllergyService allergyService;

    @Autowired
    public PatientAllergyService(PatientService patientService, AllergyService allergyService, PatientAllergyRepository patientAllergyRepository) {
        this.patientService = patientService;
        this.allergyService = allergyService;
        this.patientAllergyRepository = patientAllergyRepository;
    }

    public Set<PatientAllergy> getAllergiesFromPatient(UUID patientId) {
        Patient patient = this.patientService.getPatient(patientId);
        return patient.getAllergies();
    }

    public PatientAllergy addAllergyToPatient(UUID id, @Valid PatientAllergy patientAllergy) {
        Patient patient = this.patientService.getPatient(id);
        Allergy allergy = this.allergyService.getAllergy(patientAllergy.getAllergy().getId());

        if (this.patientAllergyRepository.findByPatientIdAndAllergyId(patient.getId(), allergy.getId()).isPresent()) {
            throw new AllergyForPatientAlreadyExistException(patient.getId(), allergy.getId());
        }
        PatientAllergy patientAllergyToAdd = new PatientAllergy(patient, allergy, patientAllergy.getNote());
        patientAllergyToAdd.setAssertedDate(patientAllergy.getAssertedDate());
        patientAllergyToAdd.setLastOccurrence(patientAllergy.getLastOccurrence());
        return this.patientAllergyRepository.save(patientAllergyToAdd);
    }

    public PatientAllergy updateAllergyToPatient(UUID patientId, UUID allergyId, @Valid PatientAllergy patientAllergy) {
        return this.patientAllergyRepository.findByPatientIdAndAllergyId(patientId, allergyId).map(existing -> {
            existing.setNote(patientAllergy.getNote());
            existing.setAssertedDate(patientAllergy.getAssertedDate());
            existing.setLastOccurrence(patientAllergy.getLastOccurrence());
            return this.patientAllergyRepository.saveAndFlush(existing);
        }).orElseThrow(() -> new AllergyForPatientNotFoundException(patientId, allergyId));
    }

    public PatientAllergy removeAllergyFromPatient(UUID patientId, UUID allergyId) {
        return this.patientAllergyRepository.findByPatientIdAndAllergyId(patientId, allergyId).map(existing -> {
            this.patientAllergyRepository.delete(existing);
            return existing;
        }).orElseThrow(() -> new AllergyForPatientNotFoundException(patientId, allergyId));
    }
}
