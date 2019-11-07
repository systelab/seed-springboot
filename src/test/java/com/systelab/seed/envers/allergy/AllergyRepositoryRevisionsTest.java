package com.systelab.seed.envers.allergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.systelab.seed.allergy.repository.AllergyRepository;
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
import com.systelab.seed.allergy.model.Allergy;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class AllergyRepositoryRevisionsTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private AllergyRepository repository;

    private Allergy allergy;

    @BeforeEach
    public void save() throws JsonParseException, JsonMappingException, IOException {

        AuthenticationHelper.mockAdminAuthentication();
        repository.deleteAll();

        allergy = repository.save(new Allergy("AllergyA", "signsA", null));

    }

    @Test
    public void initialRevision() {

        Revisions<Integer, Allergy> revisions = repository.findRevisions(allergy.getId());

        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Allergy::getId, Allergy::getName, Allergy::getSigns, Allergy::getSymptoms)
                        .containsExactly(allergy.getId(), allergy.getName(), allergy.getSigns(), allergy.getSymptoms()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }

    @Test
    public void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());
        int beforeUpdate = getTotalRevisionsById(revision);

        allergy.setName("New Allergy name");
        repository.save(allergy);

        Optional<Revision<Integer, Allergy>> revisionAfterUpdate = repository.findLastChangeRevision(allergy.getId());
        assertThat(revisionAfterUpdate).isPresent().hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber()).isNotEqualTo(beforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(Allergy::getName).isEqualTo("New Allergy name"));
    }

    @Test
    public void deletedItemWillHaveRevisionRetained() {

        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());
        int beforeUpdate = getTotalRevisionsById(revision);

        repository.delete(allergy);

        Revisions<Integer, Allergy> revisions = repository.findRevisions(allergy.getId());
        assertThat(revisions).isNotEqualTo(beforeUpdate);
        Iterator<Revision<Integer, Allergy>> iterator = revisions.iterator();
        Revision<Integer, Allergy> initialRevision = iterator.next();
        Revision<Integer, Allergy> finalRevision = iterator.next();

        assertThat(initialRevision)
                .satisfies(rev -> assertThat(rev.getEntity()).extracting(Allergy::getId, Allergy::getName, Allergy::getSigns, Allergy::getSymptoms)
                        .containsExactly(allergy.getId(), allergy.getName(), allergy.getSigns(), allergy.getSymptoms()));

        assertThat(finalRevision).satisfies(rev -> assertThat(rev.getEntity())
                .extracting(Allergy::getId, Allergy::getName, Allergy::getSigns, Allergy::getSymptoms).containsExactly(allergy.getId(), null, null, null));
    }

    @Test
    public void showAdminRevisionInformation() {

        Revisions<Integer, Allergy> revisions = repository.findRevisions(allergy.getId());
        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity()).extracting(Allergy::getId, Allergy::getName, Allergy::getSigns, Allergy::getSymptoms)
                        .containsExactly(allergy.getId(), allergy.getName(), allergy.getSigns(), allergy.getSymptoms()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                    assertThat(revisionEntity.getIpAddress()).isEqualTo("10.0.0.1");
                });
    }

    @Test
    public void checkRevisionTypeWhenDeleting() {

        repository.delete(allergy);

        AuditQuery q = getAllergyAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Allergy deletedAllergy = (Allergy) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.DEL);
        Assertions.assertNull(deletedAllergy.getSigns());
        Assertions.assertNull(deletedAllergy.getName());
        Assertions.assertNull(deletedAllergy.getSymptoms());
    }

    @Test
    public void checkRevisionTypeWhenModifying() {

        allergy.setName("New Name");
        repository.save(allergy);

        AuditQuery q = getAllergyAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Allergy modifiedAllergy = (Allergy) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.MOD);
        assertThat(modifiedAllergy.getName()).isEqualTo("New Name");
    }

    @Test
    public void checkRevisionTypeWhenCreating() {

        repository.save(new Allergy("Created Allergy name", "Created Allergy sign", "Created Allergy symptom"));

        AuditQuery q = getAllergyAuditQuery();

        List<Object[]> result = q.getResultList();

        Object[] tuple = result.get(result.size() - 1);

        Allergy createdAllergy = (Allergy) tuple[0];
        RevisionType revisionType = (RevisionType) tuple[2];

        Assertions.assertEquals(revisionType, RevisionType.ADD);
        assertThat(createdAllergy.getName()).isEqualTo("Created Allergy name");
    }

    private AuditQuery getAllergyAuditQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        AuditQuery q = auditReader.createQuery().forRevisionsOfEntity(Allergy.class, false, true);
        return q;
    }

    private int getTotalRevisionsById(Optional<Revision<Integer, Allergy>> revision) {

        int beforeUpdate = revision.get().getRevisionNumber().orElse(-1);
        return beforeUpdate;
    }
}
