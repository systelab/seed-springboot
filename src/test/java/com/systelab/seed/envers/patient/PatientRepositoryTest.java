package com.systelab.seed.envers.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.infrastructure.config.RepositoryConfig;
import com.systelab.seed.infrastructure.audit.SpringSecurityAuditorAware;
import com.systelab.seed.patient.model.Patient;
import com.systelab.seed.patient.allergy.model.PatientAllergy;
import com.systelab.seed.patient.repository.PatientRepository;


@DataJpaTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = {SpringSecurityAuditorAware.class, RepositoryConfig.class}))
@ExtendWith(SpringExtension.class)
public class PatientRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PatientRepository repository;

    private Patient patient;

    @BeforeEach
    public void save() {
        patient = em.persistAndFlush(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<PatientAllergy>()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void findAllPatients() {
        List<Patient> patients = repository.findAll();
        assertThat(patients).isNotEmpty()
                .extracting(Patient::getName, Patient::getSurname)
                .containsExactly(tuple("My Name", "My Surname"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void hasAuditInformation() {
        assertThat(patient)
                .extracting(Patient::getCreatedBy, Patient::getCreationTime, Patient::getModifiedBy, Patient::getModificationTime, Patient::getVersion)
                .isNotNull();
    }
}