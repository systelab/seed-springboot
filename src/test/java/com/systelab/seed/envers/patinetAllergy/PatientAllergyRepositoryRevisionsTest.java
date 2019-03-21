package com.systelab.seed.envers.patinetAllergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.systelab.seed.config.audit.AuditRevisionEntity;
import com.systelab.seed.envers.helper.AuthenticationHelper;
import com.systelab.seed.model.allergy.Allergy;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientAllergy;
import com.systelab.seed.repository.AllergyRepository;
import com.systelab.seed.repository.PatientAllergyRepository;
import com.systelab.seed.repository.PatientRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class PatientAllergyRepositoryRevisionsTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private PatientAllergyRepository patientAllergyRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private AllergyRepository allergyRepository;
    
    private Patient patient;    
    private Allergy allergy;
    private PatientAllergy patientAllergy;

    @BeforeEach    
    public void save() throws JsonParseException, JsonMappingException, IOException {

    	AuthenticationHelper.mockAdminAuthentication();
        patientAllergyRepository.deleteAll();
        allergyRepository.deleteAll();
        patientRepository.deleteAll();
        
        patient = createPatient();
        allergy = createAllergy();
        
        patientAllergy = createPatientAllergy(patient, allergy, "the initial note");
        patient.getAllergies().add(patientAllergy);
        patientAllergy = patientAllergyRepository.save(patientAllergy);
    }

 

    @Test    
    public void initialRevision() {
        
        Revisions<Integer, PatientAllergy> revisions = patientAllergyRepository.findRevisions(patientAllergy.getId());
        
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(PatientAllergy::getId, PatientAllergy::getNote)
                        .containsExactly(patientAllergy.getId(), patientAllergy.getNote()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }


    @Test
    public void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, PatientAllergy>> revision = patientAllergyRepository.findLastChangeRevision(patientAllergy.getId());
        int beforeUpdate = getTotalRevisionsById(revision);

        patientAllergy.setNote("the new Note");
        patientAllergyRepository.save(patientAllergy);

        Optional<Revision<Integer, PatientAllergy>> revisionAfterUpdate = patientAllergyRepository.findLastChangeRevision(patientAllergy.getId());
        assertThat(revisionAfterUpdate).isPresent()
                .hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber()).isNotEqualTo(beforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(PatientAllergy::getNote)
                        .isEqualTo("the new Note")
                );
    }

    @Test
    public void showAdminRevisionInformation() {

        Revisions<Integer, PatientAllergy> revisions = patientAllergyRepository.findRevisions(patientAllergy.getId());
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(PatientAllergy::getId, PatientAllergy::getNote)
                        .containsExactly(patientAllergy.getId(), patientAllergy.getNote()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                    assertThat(revisionEntity.getIpAddress()).isEqualTo("10.0.0.1");
                });
    }

    @Test
    public void checkRevisionTypeWhenModifying() {

        patientAllergy.setNote("New Note");
        patientAllergyRepository.save(patientAllergy);

        AuditQuery q = getPatientAllergyAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        PatientAllergy modifiedPatientAllergy = (PatientAllergy) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.MOD);
        assertThat(modifiedPatientAllergy.getNote()).isEqualTo("New Note");
    }

    @Test
    public void checkRevisionTypeWhenCreating() {

        patientAllergyRepository.save(createPatientAllergy(createPatient(), createAllergy(), "the initial note"));

        AuditQuery q = getPatientAllergyAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        PatientAllergy createdPatientAllergy = (PatientAllergy) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.ADD);
        assertThat(createdPatientAllergy.getNote()).isEqualTo("the initial note");
    }

    private AuditQuery getPatientAllergyAuditQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery q = auditReader.createQuery()
                .forRevisionsOfEntity(PatientAllergy.class, false, true);
        return q;
    }

    private int getTotalRevisionsById(Optional<Revision<Integer, PatientAllergy>> revision) {

        int beforeUpdate = revision.get()
                .getRevisionNumber()
                .orElse(-1);
        return beforeUpdate;
    }
    
    private Allergy createAllergy() {
        return allergyRepository.save(new Allergy("the Allergy", "the signs", "the sympthoms"));
    }

    private Patient createPatient() {
        return patientRepository.save(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<PatientAllergy>()));
    }   


    private PatientAllergy createPatientAllergy(Patient patient, Allergy allergy, String note) {
        return new PatientAllergy(patient, allergy, note);
    }

}
