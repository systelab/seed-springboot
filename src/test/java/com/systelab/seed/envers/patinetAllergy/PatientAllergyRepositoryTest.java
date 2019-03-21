package com.systelab.seed.envers.patinetAllergy;

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

import com.systelab.seed.config.RepositoryConfig;
import com.systelab.seed.config.audit.SpringSecurityAuditorAware;
import com.systelab.seed.model.allergy.Allergy;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.model.patient.PatientAllergy;
import com.systelab.seed.repository.PatientAllergyRepository;


@DataJpaTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = {SpringSecurityAuditorAware.class, RepositoryConfig.class}))
@ExtendWith(SpringExtension.class)
public class PatientAllergyRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PatientAllergyRepository repository;

    private Patient patient;
    private Allergy allergy;
    private PatientAllergy patientAllergy;

    @BeforeEach
    public void save() {
        patient = em.persistAndFlush(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<PatientAllergy>()));
        allergy = em.persistAndFlush(new Allergy("the Allergy", "the signs", "the sympthoms"));
        
        patientAllergy = em.persistAndFlush(new PatientAllergy(patient, allergy, "the note"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void findAllPatientAllergies() {
        List<PatientAllergy> patientAllergies = repository.findAll();
        assertThat(patientAllergies).isNotEmpty()
                .extracting(PatientAllergy::getPatient, PatientAllergy::getAllergy, PatientAllergy::getNote)
                .containsExactly(tuple(patient, allergy, "the note"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void hasAuditInformation() {
        assertThat(patientAllergy)
                .extracting(PatientAllergy::getCreatedBy, PatientAllergy::getCreationTime, PatientAllergy::getModifiedBy, PatientAllergy::getModificationTime, PatientAllergy::getVersion)
                .isNotNull();
    }
}