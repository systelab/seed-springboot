package com.systelab.seed.envers.patient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.HashSet;
import java.util.List;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.repository.PatientRepository;


@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PatientRepositoryTest {


    @Autowired
    private PatientRepository repository;

    private Patient patient;

    @BeforeEach
    public void save() {
        repository.deleteAll();
        patient = repository.save(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<>()));
        repository.flush();
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void findAllPatients() {
        List<Patient> patients = repository.findAll();
        assertThat(patients).isNotEmpty()
                .extracting(Patient::getName, Patient::getSurname)
                .containsExactly(tuple("My Name", "My Surname"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void hasAuditInformation() {
        assertThat(patient)
                .extracting(Patient::getCreatedBy, Patient::getCreationTime, Patient::getModifiedBy, Patient::getModificationTime, Patient::getVersion)
                .isNotNull();
    }
}