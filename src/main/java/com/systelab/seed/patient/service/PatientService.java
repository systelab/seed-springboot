package com.systelab.seed.patient.service;

import com.systelab.seed.patient.model.Patient;
import com.systelab.seed.patient.repository.PatientRepository;
import com.systelab.seed.service.MedicalRecordNumberService;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final MedicalRecordNumberService medicalRecordNumberService;
    private final AuditReader auditReader;
    @Autowired
    public PatientService(PatientRepository patientRepository, MedicalRecordNumberService medicalRecordNumberService, AuditReader auditReader) {
        this.patientRepository = patientRepository;
        this.medicalRecordNumberService = medicalRecordNumberService;
        this.auditReader = auditReader;
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

    public List<?> getRevisions(UUID id, boolean fetchChanges, Class<?> revisionEntityClass) {
        AuditQuery auditQuery = null;

        if (fetchChanges) {
            auditQuery = auditReader.createQuery()
                    .forRevisionsOfEntityWithChanges(revisionEntityClass, true);
        } else {
            auditQuery = auditReader.createQuery()
                    .forRevisionsOfEntity(revisionEntityClass, true);
        }
        auditQuery.add(AuditEntity.id().eq(id));
       // auditQuery.add(AuditEntity.revisionProperty("timestamp").gt("1614018311862"));
        //auditQuery.add(AuditEntity.revisionProperty("timestamp").lt(endDate);
        return auditQuery.getResultList();
    }

}
