package com.systelab.seed.envers.allergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
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
import com.systelab.seed.repository.AllergyRepository;

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
        allergy = repository.save(new Allergy("AllergyA", "signsA", "sympthoms", null));
       // allergy = repository.save(new Allergy("AllergyA", "signsA", null));
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
