package com.systelab.seed.rest.patient;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systelab.seed.config.TokenProvider;
import com.systelab.seed.model.patient.Address;
import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.repository.PatientRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientControllerTest {
	private MockMvc mvc;
	
    @Autowired
    private WebApplicationContext context; 

    @MockBean
    private TokenProvider tokenProvider;
    
    @MockBean
    private PatientRepository mockPatientRepository;    
    
    


    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllPatient() throws Exception {
    	Patient patientA = createPatient("A",1L);
    	Patient patientB = createPatient("B",2L);
    	List<Patient> patients = Arrays.asList(patientA,
    			patientB);
    	
    	when(mockPatientRepository.findAll()).thenReturn(patients);
    	
    	mvc.perform(get("/seed/v1/patients")
    		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
	    	.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.[0].id", is(1)))
			.andExpect(jsonPath("$.[1].id",is(2)))
			.andExpect(jsonPath("$.[0].name",is("patientA")));
		

    }
    @Test
    @WithMockUser(roles = "USER")
    public void testGetPatient() throws Exception {
    	Optional<Patient> patient=  Optional.of(createPatient("A",1L));
		   
		when(mockPatientRepository.findById(isA(Long.class))).thenReturn(patient);
		
	     mvc.perform(get("/seed/v1/patients/{id}",1L)
			.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id",is(1)))
			.andExpect(jsonPath("$.surname",is("surnameA")));

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInsertPatient() throws Exception {
    	Patient patient =  createPatient("A", 1L);

        
        when(mockPatientRepository.save(any())).thenReturn(patient);
        
        mvc.perform(post("/seed/v1/patients/patient")
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a")
        		.contentType(MediaType.APPLICATION_JSON).content(createPatientInJson(patient)))
                .andExpect(status().is2xxSuccessful());

    }
    
    @Test
    @WithMockUser(roles = "User")
    public void testDeletePatient() throws Exception {
    	Optional<Patient> patient=  Optional.of(createPatient("A",1L));   
    	when(mockPatientRepository.findById(isA(Long.class))).thenReturn(patient);
        
        mvc.perform(delete("/seed/v1/patients/{1}",1L)
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(status().is2xxSuccessful());

    }
    

	private static String createPatientInJson(Patient patient) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper(); 
		return mapper.writeValueAsString(patient);
	}


	private Patient createPatient(String patientName,Long id) {
		Patient patient = new Patient();
    	patient.setId(id);
    	patient.setName("patient" + patientName);
    	patient.setSurname("surname" + patientName);
    	patient.setEmail("patient" + patientName + "@systelab.com");
    	patient.setDob(new Date());
    	Address address= new Address();
    	address.setCity("city" + patientName);
    	address.setCoordinates("coordinates" + patientName);
    	address.setStreet("street" + patientName);
    	address.setZip("zip" + patientName);
    	patient.setAddress(address);
		return patient;
	}
    

}
