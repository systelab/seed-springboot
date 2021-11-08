package com.systelab.seed.rest.allergy;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.systelab.seed.features.allergy.repository.AllergyRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systelab.seed.core.security.config.TokenProvider;
import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class AllergyControllerTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AllergyRepository mockAllergyRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllAllergies() throws Exception {
        
        Allergy allergyA = createAllergy("A");
        Allergy allergyB = createAllergy("B");
        
        Patient patientA = createPatient("patientA");
        Patient patientB = createPatient("patientB");
       
        
        Set<PatientAllergy> patientAllergies = new HashSet<>();
        patientAllergies.add(new PatientAllergy(patientA, allergyA, "note Allergy A patient A"));
        
        patientAllergies.add(new PatientAllergy(patientB, allergyA, "note Allergy A patient B"));

        List<Allergy> allergies = Arrays.asList(allergyA, allergyB);
        
        Page<Allergy> pageofAllergy = new PageImpl<>(allergies);

        when(mockAllergyRepository.findAll(isA(Pageable.class))).thenReturn(pageofAllergy);

        mvc.perform(get("/seed/v1/allergies")
                .header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content[1].id", is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13")))
                .andExpect(jsonPath("$.content[0].name", is("allergyA")));
    }

   
    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllergy() throws Exception {
        Optional<Allergy> allergy = Optional.of(createAllergy("A"));

        when(mockAllergyRepository.findById(isA(UUID.class))).thenReturn(allergy);

        mvc.perform(get("/seed/v1/allergies/{id}", "a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
                .header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13")))
                .andExpect(jsonPath("$.name", is("allergyA")));
    }
    
    

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInsertAllergy() throws Exception {
        Allergy allergy = createAllergy("A");

        when(mockAllergyRepository.save(any())).thenReturn(allergy);

        mvc.perform(post("/seed/v1/allergies/allergy")
                .header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a")
                .contentType(MediaType.APPLICATION_JSON).content(createAllergyInJson(allergy)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser(roles = "User")
    public void testDeleteAllergy() throws Exception {
        Optional<Allergy> allergy = Optional.of(createAllergy("A"));
        when(mockAllergyRepository.findById(isA(UUID.class))).thenReturn(allergy);

        mvc.perform(delete("/seed/v1/allergies/{1}", "a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
                .header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(status().is2xxSuccessful());

    }

    private static String createAllergyInJson(Allergy allergy) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(allergy);
    }

    private Patient createPatient(String patientName) {
        Patient patient = new Patient();
        patient.setName("Name " + patientName);
        return patient;
    }

    
    private Allergy createAllergy(String allergyName) {
        
        Allergy allergy = new Allergy();
        allergy.setId(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"));
        allergy.setName("allergy" + allergyName);
        allergy.setSigns("signs" + allergyName);
        
        return allergy;
    }
}
