package com.systelab.seed.envers.patient.allergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Optional;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.core.audit.AuditRevisionEntity;
import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.repository.PatientAllergyRepository;
import com.systelab.seed.features.patient.repository.PatientRepository;

@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@SpringBootTest
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PatientAllergyRepositoryRevisionsTest {

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
    void save()  {
        patientAllergyRepository.deleteAll();
        allergyRepository.deleteAll();
        patientRepository.deleteAll();
        
        patient = createPatient();
        allergy = createAllergy();
        patientAllergy = patientAllergyRepository.save(new PatientAllergy(patient, allergy, "the initial note"));
        patientAllergyRepository.flush();

    }


    @Test    
    void initialRevision() {
        
        Revisions<Integer, PatientAllergy> revisions = patientAllergyRepository.findRevisions(patientAllergy.getId());
        
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(PatientAllergy::getId, PatientAllergy::getNote)
                        .containsExactly(patientAllergy.getId(), patientAllergy.getNote()))
                .allSatisfy(revision -> {
                    RevisionMetadata<Integer> metadata = revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }


    @Test
    void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, PatientAllergy>> revision = patientAllergyRepository.findLastChangeRevision(patientAllergy.getId());
        int numberOfReleasesBeforeUpdate = revision.get().getRevisionNumber().orElse(-1);

        patientAllergy.setNote("the new Note");
        patientAllergyRepository.save(patientAllergy);

        Optional<Revision<Integer, PatientAllergy>> revisionAfterUpdate = patientAllergyRepository.findLastChangeRevision(patientAllergy.getId());
        assertThat(revisionAfterUpdate).isPresent()
                .hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber().get()).isNotEqualTo(numberOfReleasesBeforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(PatientAllergy::getNote)
                        .isEqualTo("the new Note")
                );
    }

    @Test
    void showAdminRevisionInformation() {

        Revisions<Integer, PatientAllergy> revisions = patientAllergyRepository.findRevisions(patientAllergy.getId());
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(PatientAllergy::getId, PatientAllergy::getNote)
                        .containsExactly(patientAllergy.getId(), patientAllergy.getNote()))
                .allSatisfy(revision -> {
                    RevisionMetadata<Integer> metadata = revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                    assertThat(revisionEntity.getIpAddress()).isEqualTo("10.0.0.1");
                });
    }

    @Test
    void checkRevisionTypeWhenModifying() {

        patientAllergy.setNote("New Note");
        patientAllergyRepository.save(patientAllergy);

        Optional<Revision<Integer, PatientAllergy>> revision = patientAllergyRepository.findLastChangeRevision(patientAllergy.getId());

        PatientAllergy modifiedPatientAllergy = revision.get().getEntity();

        assertThat(modifiedPatientAllergy.getNote()).isEqualTo("New Note");
        Assertions.assertEquals(RevisionMetadata.RevisionType.UPDATE, revision.get().getMetadata().getRevisionType());
    }

    @Test
    void checkRevisionTypeWhenCreating() {

        PatientAllergy newPatientAllergy = patientAllergyRepository.save(new PatientAllergy(createPatient(), createAllergy(), "another note"));

        Optional<Revision<Integer, PatientAllergy>> revision = patientAllergyRepository.findLastChangeRevision(newPatientAllergy.getId());

        PatientAllergy createdPatientAllergy = revision.get().getEntity();

        assertThat(createdPatientAllergy.getNote()).isEqualTo("another note");
        Assertions.assertEquals(RevisionMetadata.RevisionType.INSERT, revision.get().getMetadata().getRevisionType());
    }

    private Allergy createAllergy() {
        return allergyRepository.saveAndFlush(new Allergy("the allergy", "the signs", "the symptoms"));

    }

    private Patient createPatient() {
        return patientRepository.saveAndFlush(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<PatientAllergy>()));
    }


}
