package com.systelab.seed.audit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit4.SpringRunner;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.repository.PatientRepository;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@RunWith(SpringRunner.class)
public class SpringDataAuditPatientTest {
	
    private static final String ADMIN_USER = "Admin";

	@Autowired
    private PatientRepository patientRepository;

    private Patient patient;

    @Before
    public void createPatientByAdmin() {
    	
    	Patient patientA = createPatient("Darran");    	
    	patientA.setCreatedBy(ADMIN_USER);
    	patientA.setModifiedBy(ADMIN_USER);
    	patient = patientRepository.save(patientA);
    	
        assertThat(patient.getCreatedBy())
            .isNotNull();

        assertThat(patient.getModifiedBy())
            .isNotNull();

        assertThat(patient.getCreatedBy())
                .isEqualTo(ADMIN_USER);

        assertThat(patient.getModifiedBy())
                .isEqualTo(ADMIN_USER);
    }

    @Test   
    public void updatePatient() {
        Timestamp created = patient.getCreationTime();        
        Timestamp modified = patient.getUpdateTime();
        
        patientRepository.findById(patient.getId())
        .ifPresent(updatedUser -> {        	 
        	
        	 updatedUser.setName("UpdatedDarran");
        	 patientRepository.save(updatedUser);
        	   	
        });
        
        patientRepository.findById(patient.getId())
                .ifPresent(updatedUser -> {
                	
                    assertThat(updatedUser.getName())
                            .isEqualTo("UpdatedDarran");
                    
                    assertThat(patient.getCreationTime())
                    .isEqualTo(created);
                    
                    patientRepository.flush();     
        
                    assertThat(updatedUser.getUpdateTime())
                            .isAfter(modified);
                });
    }
    
    
    private Patient createPatient(String patientName) {
        Patient patient = new Patient();
        patient.setId(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"));
        patient.setName("patient" + patientName);
        patient.setSurname("surname" + patientName);
        patient.setEmail("patient" + patientName + "@systelab.com");
        patient.setDob(new Date());
        Address address = new Address();
        address.setCity("city" + patientName);
        address.setCoordinates("coordinates" + patientName);
        address.setStreet("street" + patientName);
        address.setZip("zip" + patientName);
        patient.setAddress(address);
        return patient;
    }
    
}
