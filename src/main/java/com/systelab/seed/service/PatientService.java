package com.systelab.seed.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.systelab.seed.model.allergy.Allergy;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientAllergy;
import com.systelab.seed.model.wrapper.AllergyNote;
import com.systelab.seed.repository.PatientNotFoundException;
import com.systelab.seed.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AllergyService allergyService;

    private final MedicalRecordNumberService medicalRecordNumberService;

    @Autowired
    public PatientService(PatientRepository patientRepository, MedicalRecordNumberService medicalRecordNumberService, AllergyService allergyService) {
        this.patientRepository = patientRepository;
        this.allergyService = allergyService;
        this.medicalRecordNumberService = medicalRecordNumberService;

    }

    public Page<Patient> getAllPatients(Pageable pageable) {
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "surname", "name");
        return this.patientRepository.findAll(page);
    }

    public Patient getPatient(UUID patientId) {
        return this.patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    public Patient createPatient(Patient p) {
        if (p.getMedicalNumber() == null || p.getMedicalNumber().equals("")) {
            p.setMedicalNumber(medicalRecordNumberService.getMedicalRecordNumber());
        }
        return this.patientRepository.save(p);
    }

    public Patient updatePatient(UUID id, Patient p) {
        return this.patientRepository.findById(id).map(existing -> {
            p.setId(id);
            return this.patientRepository.save(p);
        }).orElseThrow(() -> new PatientNotFoundException(id));
    }

    public Patient removePatient(UUID id) {
        return this.patientRepository.findById(id).map(existing -> {
            patientRepository.delete(existing);
            return existing;
        }).orElseThrow(() -> new PatientNotFoundException(id));
    }

    public Patient addAlergyToPatient(UUID patientId, UUID allergyId, @Valid PatientAllergy patientAllergytoAdd) {

        Patient patient = this.getPatient(patientId);
        Allergy allergy = this.allergyService.getAllergy(allergyId);

        PatientAllergy patientAllergy = new PatientAllergy(patient, allergy, patientAllergytoAdd.getNote());
        patientAllergy.setAssertedDate(patientAllergytoAdd.getAssertedDate());
        patientAllergy.setLastOcurrence(patientAllergytoAdd.getLastOcurrence());

        patient.getAllergies().add(patientAllergy);

        return this.patientRepository.save(patient);
    }

    public Set<Allergy> getAllergiesFromPatient(UUID patientId) {
        Set<Allergy> allergies = new HashSet<>();
        Patient patient = this.getPatient(patientId);
        patient.getAllergies().stream().forEach(pa -> allergies.add(pa.getAllergy()));

        return allergies;
    }

    public Patient createAllergyToPatient(UUID id, @Valid AllergyNote a) {
        Patient patient = this.getPatient(id);
        Allergy allergy = this.allergyService.createAllergy(a.getAllergy());
        PatientAllergy patientAllergy = new PatientAllergy(patient, allergy, a.getNote());

        patient.getAllergies().add(patientAllergy);

        return this.patientRepository.save(patient);
    }

    public Patient removeAlleryFromPatient(UUID patientId, UUID allergyId) {

        Patient patient = this.getPatient(patientId);
        patient.getAllergies().removeIf(pa -> {
            if (pa.getAllergy().getId().equals(allergyId)) {
                pa.setAllergy(null);
                pa.setPatient(null);
                return true;
            } else {
                return false;
            }
        });

        return this.patientRepository.saveAndFlush(patient);

    }

}
