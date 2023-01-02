package com.systelab.seed.envers.allergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.core.audit.AuditRevisionEntity;
import com.systelab.seed.features.allergy.model.Allergy;

@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@SpringBootTest
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AllergyRepositoryRevisionsTest {

    @Autowired
    private AllergyRepository repository;

    private Allergy allergy;

    @BeforeEach
    void save() {
        repository.deleteAll();
        allergy = repository.save(new Allergy("AllergyA", "signsA", null));
        repository.flush();
    }


    @Test
    void initialRevision() {

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
    void updateIncreasesRevisionNumber() {
        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());
        int numberOfRevisionsBeforeUpdate = revision.flatMap(Revision::getRevisionNumber).orElse(-1);

        allergy.setName("New Allergy name");
        repository.save(allergy);

        Optional<Revision<Integer, Allergy>> revisionAfterUpdate = repository.findLastChangeRevision(allergy.getId());
        assertThat(revisionAfterUpdate).isPresent().hasValueSatisfying(rev -> assertThat(rev.getRevisionNumber().get()).isNotEqualTo(numberOfRevisionsBeforeUpdate))
                .hasValueSatisfying(rev -> assertThat(rev.getEntity()).extracting(Allergy::getName).isEqualTo("New Allergy name"));
    }

    @Test
    void deletedItemWillHaveRevisionRetained() {

        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());
        int numberOfRevisionsBeforeUpdate = revision.flatMap(Revision::getRevisionNumber).orElse(-1);

        repository.delete(allergy);

        Revisions<Integer, Allergy> revisions = repository.findRevisions(allergy.getId());
        assertThat(revisions.stream().count()).isNotEqualTo(numberOfRevisionsBeforeUpdate);
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
    void showAdminRevisionInformation() {

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
    void checkRevisionTypeWhenDeleting() {

        repository.delete(allergy);

        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());

        Allergy deletedAllergy = revision.get().getEntity();

        Assertions.assertNull(deletedAllergy.getSigns());
        Assertions.assertNull(deletedAllergy.getName());
        Assertions.assertNull(deletedAllergy.getSymptoms());
        Assertions.assertEquals(RevisionMetadata.RevisionType.DELETE, revision.get().getMetadata().getRevisionType());
    }

    @Test
    void checkRevisionTypeWhenModifying() {

        allergy.setName("New Name");
        repository.save(allergy);

        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(allergy.getId());

        Allergy modifiedAllergy = revision.get().getEntity();

        assertThat(modifiedAllergy.getName()).isEqualTo("New Name");
        Assertions.assertEquals(RevisionMetadata.RevisionType.UPDATE, revision.get().getMetadata().getRevisionType());
    }

    @Test
    void checkRevisionTypeWhenCreating() {

        Allergy newAllergy = repository.save(new Allergy("Created Allergy name", "Created Allergy sign", "Created Allergy symptom"));

        Optional<Revision<Integer, Allergy>> revision = repository.findLastChangeRevision(newAllergy.getId());

        Allergy createdAllergy = revision.get().getEntity();

        assertThat(createdAllergy.getName()).isEqualTo("Created Allergy name");
        Assertions.assertEquals(RevisionMetadata.RevisionType.INSERT, revision.get().getMetadata().getRevisionType());
    }

}
