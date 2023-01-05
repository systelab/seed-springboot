package com.systelab.seed.envers.patient;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.core.audit.AuditRevisionEntity;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.repository.PatientRepository;

@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@SpringBootTest
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PatientRepositoryRevisionsTest {

    @Autowired
    private PatientRepository repository;

    private Patient patient;

    @BeforeEach
    public void save() throws IOException {
        repository.deleteAll();
        patient = repository.save(new Patient("My Surname", "My Name", null, null, null, null, null));
        repository.flush();
    }


    @Test
    void initialRevision() {

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());

        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), patient.getName(), patient.getSurname()))
                .allSatisfy(revision -> {
                    RevisionMetadata<Integer> metadata = revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }


    @Test
    void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());
        int numberOfRevisionsBeforeUpdate = revision.flatMap(Revision::getRevisionNumber).orElse(-1);

        patient.setName("New Name");
        repository.save(patient);

        Optional<Revision<Integer, Patient>> revisionAfterUpdate = repository.findLastChangeRevision(patient.getId());
        assertThat(revisionAfterUpdate).isPresent()
                .hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber().get()).isNotEqualTo(numberOfRevisionsBeforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(Patient::getName)
                        .isEqualTo("New Name")
                );
    }

    @Test
    void deletedItemWillHaveRevisionRetained() {

        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());
        int numberOfRevisionsBeforeUpdate = revision.flatMap(Revision::getRevisionNumber).orElse(-1);

        repository.delete(patient);

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());
        assertThat(revisions.stream().count()).isNotEqualTo(numberOfRevisionsBeforeUpdate);
        Iterator<Revision<Integer, Patient>> iterator = revisions.iterator();
        Revision<Integer, Patient> initialRevision = iterator.next();
        Revision<Integer, Patient> finalRevision = iterator.next();

        assertThat(initialRevision)
                .satisfies(rev -> assertThat(rev.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), patient.getName(), patient.getSurname())
                );

        assertThat(finalRevision)
                .satisfies(rev -> assertThat(rev.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), null, null));
    }


    @Test
    void showAdminRevisionInformation() {

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), patient.getName(), patient.getSurname()))
                .allSatisfy(revision -> {
                    RevisionMetadata<Integer> metadata = revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                    assertThat(revisionEntity.getIpAddress()).isEqualTo("10.0.0.1");
                });
    }

    @Test
    void checkRevisionTypeWhenDeleting() {

        repository.delete(patient);

        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());

        Patient deletedPatient = revision.get().getEntity();

        Assertions.assertNull(deletedPatient.getAddress());
        Assertions.assertNull(deletedPatient.getName());
        Assertions.assertNull(deletedPatient.getSurname());
        Assertions.assertEquals(RevisionMetadata.RevisionType.DELETE, revision.get().getMetadata().getRevisionType());
    }

    @Test
    void checkRevisionTypeWhenModifying() {

        patient.setName("New Name");
        repository.save(patient);

        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());

        Patient modifiedPatient = revision.get().getEntity();

        assertThat(modifiedPatient.getName()).isEqualTo("New Name");
        Assertions.assertEquals(RevisionMetadata.RevisionType.UPDATE, revision.get().getMetadata().getRevisionType());
    }

    @Test
    void checkRevisionTypeWhenCreating() {

        Patient newPatient = repository.save(new Patient("Created Patient Surname", "Created Patient Name", null, null, null, null, null));

        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(newPatient.getId());

        Patient createdPatient = revision.get().getEntity();

        assertThat(createdPatient.getName()).isEqualTo("Created Patient Name");
        Assertions.assertEquals(RevisionMetadata.RevisionType.INSERT, revision.get().getMetadata().getRevisionType());
    }


}
