package com.systelab.seed.envers.patient.allergy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import com.systelab.seed.features.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.repository.PatientAllergyRepository;

@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PatientAllergyRepositoryTest {

    @Autowired
    private PatientAllergyRepository repository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    private Patient patient;
    private Allergy allergy;
    private PatientAllergy patientAllergy;

    @BeforeEach
    void save() {
        patientRepository.deleteAll();
        allergyRepository.deleteAll();
        repository.deleteAll();

        patient=patientRepository.saveAndFlush(new Patient("My Surname", "My Name", null, null, null, null, new HashSet<>()));
        allergy=allergyRepository.saveAndFlush(new Allergy("the allergy", "the signs", "the symptoms"));

        patientAllergy = repository.save(new PatientAllergy(patient, allergy, "the note"));
        repository.flush();
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void findAllPatientAllergies() {
        List<PatientAllergy> patientAllergies = repository.findAll();
        assertThat(patientAllergies).isNotEmpty();
        assertThat(patientAllergies.get(0).getPatient().getName()).isEqualTo("My Name");
        assertThat(patientAllergies.get(0).getAllergy().getName()).isEqualTo("the allergy");
        assertThat(patientAllergies.get(0).getNote()).isEqualTo("the note");
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void hasAuditInformation() {
        assertThat(patientAllergy)
                .extracting(PatientAllergy::getCreatedBy, PatientAllergy::getCreationTime, PatientAllergy::getModifiedBy, PatientAllergy::getModificationTime, PatientAllergy::getVersion)
                .isNotNull();
    }
}