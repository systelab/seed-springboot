package com.systelab.seed.envers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.systelab.seed.audit.config.AuditConfiguration;
import com.systelab.seed.audit.config.AuditorAwareImpl;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.repository.PatientRepository;
import com.systelab.seed.repository.RepositoryConfiguration;


@DataJpaTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = { AuditorAwareImpl.class, AuditConfiguration.class, RepositoryConfiguration.class }))
@RunWith(SpringRunner.class)
public class PatientRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PatientRepository repository;

    private Patient patient;

    @Before
    public void save() {

        patient = em.persistAndFlush(Patient.builder()
                .name("My Name")
                .surname("My Surname")
                .build()
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void findAllByAuthor() {
        List<Patient> booksByAuthor = repository.findAll();
        assertThat(booksByAuthor).isNotEmpty()
                .extracting(Patient::getName, Patient::getSurname)
                .containsExactly(tuple("My Name", "My Surname"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void hasAuditInformation() {
        assertThat(patient)
                .extracting(Patient::getCreatedBy, Patient::getCreationTime, Patient::getModifiedBy, Patient::getUpdateTime, Patient::getVersion)
                .isNotNull();
    }
}