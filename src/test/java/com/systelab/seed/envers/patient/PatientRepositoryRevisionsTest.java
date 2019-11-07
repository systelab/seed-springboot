package com.systelab.seed.envers.patient;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Iterator;
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
import com.systelab.seed.infrastructure.audit.AuditRevisionEntity;
import com.systelab.seed.envers.helper.AuthenticationHelper;
import com.systelab.seed.patient.model.Patient;
import com.systelab.seed.patient.repository.PatientRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class PatientRepositoryRevisionsTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private PatientRepository repository;

    private Patient patient;

    @BeforeEach
    public void save() throws JsonParseException, JsonMappingException, IOException {

    	AuthenticationHelper.mockAdminAuthentication();
        repository.deleteAll();
        patient = repository.save(new Patient("My Surname", "My Name", null, null, null, null, null));
    }

    @Test
    public void initialRevision() {

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());

        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), patient.getName(), patient.getSurname()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }


    @Test
    public void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());
        int beforeUpdate = getTotalRevisionsById(revision);

        patient.setName("New Name");
        repository.save(patient);

        Optional<Revision<Integer, Patient>> revisionAfterUpdate = repository.findLastChangeRevision(patient.getId());
        assertThat(revisionAfterUpdate).isPresent()
                .hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber()).isNotEqualTo(beforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(Patient::getName)
                        .isEqualTo("New Name")
                );
    }

    @Test
    public void deletedItemWillHaveRevisionRetained() {

        Optional<Revision<Integer, Patient>> revision = repository.findLastChangeRevision(patient.getId());
        int beforeUpdate = getTotalRevisionsById(revision);

        repository.delete(patient);

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());
        assertThat(revisions).isNotEqualTo(beforeUpdate);
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
    public void showAdminRevisionInformation() {

        Revisions<Integer, Patient> revisions = repository.findRevisions(patient.getId());
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Patient::getId, Patient::getName, Patient::getSurname)
                        .containsExactly(patient.getId(), patient.getName(), patient.getSurname()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                    assertThat(revisionEntity.getIpAddress()).isEqualTo("10.0.0.1");
                });
    }

    @Test
    public void checkRevisionTypeWhenDeleting() {

        repository.delete(patient);

        AuditQuery q = getPatientAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Patient deletedPatient = (Patient) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.DEL);
        Assertions.assertNull(deletedPatient.getAddress());
        Assertions.assertNull(deletedPatient.getName());
        Assertions.assertNull(deletedPatient.getSurname());
    }

    @Test
    public void checkRevisionTypeWhenModifying() {

        patient.setName("New Name");
        repository.save(patient);

        AuditQuery q = getPatientAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Patient modifiedPatient = (Patient) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.MOD);
        assertThat(modifiedPatient.getName()).isEqualTo("New Name");
    }

    @Test
    public void checkRevisionTypeWhenCreating() {

        repository.save(new Patient("Created Patient Surname", "Created Patient Name", null, null, null, null, null));

        AuditQuery q = getPatientAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Patient createdPatient = (Patient) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.ADD);
        assertThat(createdPatient.getName()).isEqualTo("Created Patient Name");
    }

    private AuditQuery getPatientAuditQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery q = auditReader.createQuery()
                .forRevisionsOfEntity(Patient.class, false, true);
        return q;
    }

    private int getTotalRevisionsById(Optional<Revision<Integer, Patient>> revision) {

        int beforeUpdate = revision.get()
                .getRevisionNumber()
                .orElse(-1);
        return beforeUpdate;
    }   
}
