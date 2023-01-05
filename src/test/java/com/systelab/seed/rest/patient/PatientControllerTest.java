package com.systelab.seed.rest.patient;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.systelab.seed.core.security.config.TokenProvider;
import com.systelab.seed.features.patient.model.Address;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.repository.PatientRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PatientControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private PatientRepository mockPatientRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllPatient() throws Exception {
        Patient patientA = createPatient("A");
        Patient patientB = createPatient("B");
        List<Patient> patients = Arrays.asList(patientA, patientB);

        Page<Patient> pageofPatient = new PageImpl<>(patients);

        when(mockPatientRepository.findAll(isA(Pageable.class))).thenReturn(pageofPatient);

        mvc.perform(get("/seed/v1/patients").header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content[1].id", is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"))).andExpect(jsonPath("$.content[0].name", is("patientA")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetPatient() throws Exception {
        Optional<Patient> patient = Optional.of(createPatient("A"));

        when(mockPatientRepository.findById(isA(UUID.class))).thenReturn(patient);

        mvc.perform(get("/seed/v1/patients/{id}", "a98b8fe5-7cc5-4348-8f99-4860f5b84b13").header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"))).andExpect(jsonPath("$.surname", is("surnameA")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testInsertPatient() throws Exception {
        Patient patient = createPatient("A");

        when(mockPatientRepository.save(any())).thenReturn(patient);

        mvc.perform(post("/seed/v1/patients/patient").header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a")
                .contentType(MediaType.APPLICATION_JSON).content(createPatientInJson(patient))).andDo(print()).andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser(roles = "User")
    void testDeletePatient() throws Exception {
        Optional<Patient> patient = Optional.of(createPatient("A"));
        when(mockPatientRepository.findById(isA(UUID.class))).thenReturn(patient);

        mvc.perform(
                delete("/seed/v1/patients/{1}", "a98b8fe5-7cc5-4348-8f99-4860f5b84b13").header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(status().is2xxSuccessful());

    }

    private static String createPatientInJson(Patient patient) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(patient);
    }

    private Patient createPatient(String patientName) {
        Patient patient = new Patient();
        patient.setId(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"));
        patient.setName("patient" + patientName);
        patient.setSurname("surname" + patientName);
        patient.setEmail("patient" + patientName + "@systelab.com");
        patient.setDob(LocalDate.now());
        Address address = new Address();
        address.setCity("city" + patientName);
        address.setCoordinates("coordinates" + patientName);
        address.setStreet("street" + patientName);
        address.setZip("zip" + patientName);
        patient.setAddress(address);
        return patient;
    }
}
