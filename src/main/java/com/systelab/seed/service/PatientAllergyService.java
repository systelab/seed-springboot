package com.systelab.seed.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.systelab.seed.model.allergy.Allergy;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientAllergy;
import com.systelab.seed.repository.PatientAllergyRepository;
import com.systelab.seed.repository.PatientNotFoundException;

@Service
public class PatientAllergyService {

    private final PatientService patientService;
    private final PatientAllergyRepository patientAllergyRepository;
    private final AllergyService allergyService;

    @Autowired
    public PatientAllergyService(PatientService patientService, AllergyService allergyService, PatientAllergyRepository pateintAllergyRepository) {
        this.patientService = patientService;
        this.allergyService = allergyService;
        this.patientAllergyRepository = pateintAllergyRepository;

    }

    public PatientAllergy updateAllergyToPatient(UUID patientId, UUID allergyId, @Valid PatientAllergy patientAllergytoAdd) {

        Patient patient = this.patientService.getPatient(patientId);

        List<PatientAllergy> patientAllergies = patient.getAllergies().stream().filter(pa -> pa.getAllergy().getId().equals(allergyId))
                .collect(Collectors.toList());
        if (patientAllergies.isEmpty() || patientAllergies.size() > 1) {
            throw new PatientNotFoundException(patientId);
        }

        PatientAllergy patientAllergyToUpdate = patientAllergies.get(0);

        patientAllergyToUpdate.setNote(patientAllergytoAdd.getNote());
        patientAllergyToUpdate.setAssertedDate(patientAllergytoAdd.getAssertedDate());
        patientAllergyToUpdate.setLastOccurrence(patientAllergytoAdd.getLastOccurrence());

        return this.patientAllergyRepository.saveAndFlush(patientAllergyToUpdate);
    }

    public Set<PatientAllergy> getAllergiesFromPatient(UUID patientId) {
        Patient patient = this.patientService.getPatient(patientId);
        return patient.getAllergies();
    }

    public PatientAllergy createAllergyToPatient(UUID id, @Valid PatientAllergy pa) {
        Patient patient = this.patientService.getPatient(id);
        Allergy allergy = this.allergyService.getAllergy(pa.getAllergy().getId());

        List<PatientAllergy> patientAllergiesFiltered = patient.getAllergies().stream()
                .filter(pastream -> pastream.getAllergy().getId().equals(allergy.getId())).collect(Collectors.toList());

        if (!patientAllergiesFiltered.isEmpty()) {
            throw new PatientNotFoundException(id);
        }

        PatientAllergy patientAllergyToAdd = new PatientAllergy(patient, allergy, pa.getNote());
        patientAllergyToAdd.setAssertedDate(pa.getAssertedDate());
        patientAllergyToAdd.setLastOccurrence(pa.getLastOccurrence());

        patient.getAllergies().add(patientAllergyToAdd);

        return this.patientAllergyRepository.save(patientAllergyToAdd);
    }

    public void removeAllergyFromPatient(UUID patientId, UUID allergyId) {

        Patient patient = this.patientService.getPatient(patientId);

        List<PatientAllergy> patientAllergies = patient.getAllergies().stream().filter(pa -> pa.getAllergy().getId().equals(allergyId))
                .collect(Collectors.toList());
        if (patientAllergies.isEmpty() || patientAllergies.size() > 1) {
            throw new PatientNotFoundException(patientId);
        }

        Set<PatientAllergy> newPateintAllergies = patient.getAllergies().stream().filter(pa -> !(pa.getAllergy().getId().equals(allergyId)))
                .collect(Collectors.toSet());
        patient.setAllergies(newPateintAllergies);
        PatientAllergy patientAllergyToDelte = patientAllergies.get(0);

        this.patientAllergyRepository.delete(patientAllergyToDelte);

    }

}
