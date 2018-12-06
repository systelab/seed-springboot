package com.systelab.seed.rest.user;

import static org.hamcrest.Matchers.hasSize;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systelab.seed.config.authentication.TokenProvider;
import com.systelab.seed.model.user.User;
import com.systelab.seed.model.user.UserRole;
import com.systelab.seed.repository.UserRepository;
import com.systelab.seed.service.AppUserDetailsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
	
	private MockMvc mvc;
	
    @Autowired
    private WebApplicationContext context; 

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AppUserDetailsService userDetailsService;
    
    @MockBean
    private UserRepository mockUserRepository;    
    
    @MockBean
    private AuthenticationManager authenticationManager;    

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testFindUsersAuthoritzation() throws Exception {
    	//Mock Data to generate some users
		List<User> users = Arrays.asList(new User(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"), "Ivano", "Balic","Balic","Best"),
		new User(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"), "Jackson", "Richardson","Jackson","Rastas"));
		Page<User> pageofUser = new PageImpl<>(users);		
		   
		when(mockUserRepository.findAll(isA(Pageable.class))).thenReturn(pageofUser);
		
	     mvc.perform(get("/seed/v1/users")
			.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.content", hasSize(2)))
			.andExpect(jsonPath("$.content[0].id",is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13")))
			.andExpect(jsonPath("$.content[0].login",is("Balic")));

    }

    @Test
    @WithAnonymousUser
    public void shouldGetUnauthorizedWithAnonymousUser() throws Exception {    	

        mvc.perform(get("/seed/v1/users"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testFindUser() throws Exception {
    	Optional<User> user=  Optional.of(new User(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"), "Daenerys", "Targaryen","Daenerys","Dragons"));
		   
		when(mockUserRepository.findById(isA(UUID.class))).thenReturn(user);
		
	     mvc.perform(get("/seed/v1/users/{id}","a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
			.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id",is("a98b8fe5-7cc5-4348-8f99-4860f5b84b13")))
			.andExpect(jsonPath("$.login",is("Daenerys")));

    }
    
    @Test
    @WithAnonymousUser
    public void testFindUserUnauthorized() throws Exception {
    	Optional<User> user=  Optional.of(new User(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"), "Daenerys", "Targaryen","Daenerys","Dragons"));
		   
		when(mockUserRepository.findById(isA(UUID.class))).thenReturn(user);
		
	     mvc.perform(get("/seed/v1/users/{id}","a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
			.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))			
			.andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testInsertUser() throws Exception {

    	User user = new User();
        user.setLogin("Systelab");
        user.setName("name");
        user.setSurname("surname");
        user.setPassword("Systelab");
        user.setRole(UserRole.ADMIN);  
    	
        
        when(mockUserRepository.save(any())).thenReturn(user);
        
        mvc.perform(post("/seed/v1/users/user")
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a")
        		.contentType(MediaType.APPLICATION_JSON).content(createUserInJson(user)))
                .andExpect(status().is2xxSuccessful());

    }
    
    @Test
    @WithMockUser(authorities = "User")
    public void testInsertUserUnauthorized() throws Exception {

        User user = new User();
        user.setLogin("login");
        user.setName("name");
        user.setSurname("surname");
        user.setRole(UserRole.USER);    
       
        when(mockUserRepository.save(any())).thenReturn(user);   
      
        mvc.perform(post("/seed/v1/users/user")
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a")
        		.contentType(MediaType.APPLICATION_JSON).content(createUserInJson(user)))
                .andExpect(status().isForbidden());
        
    }
    
    @Test
    @WithMockUser(authorities="ADMIN")
    public void testDeleteUser() throws Exception {

    	Optional<User> user=  Optional.of(new User(UUID.fromString("a98b8fe5-7cc5-4348-8f99-4860f5b84b13"), "Nikola", "Karabtic","Leonidas","Handball"));
		   
		when(mockUserRepository.findById(isA(UUID.class))).thenReturn(user);
        
        mvc.perform(delete("/seed/v1/users/{id}","a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(status().is2xxSuccessful());

    }
    
    @Test
    @WithMockUser(authorities = "User")
    public void testDeleteUserUnauthorized() throws Exception {

        mvc.perform(delete("/seed/v1/users/{id}","a98b8fe5-7cc5-4348-8f99-4860f5b84b13")
        		.header("Authorization", "Bearer 5d1103e-b3e1-4ae9-b606-46c9c1bc915a"))
                .andExpect(status().isForbidden());

    }
    
    private static String createUserInJson (User user) throws JsonProcessingException {
    	ObjectMapper mapper = new ObjectMapper();
    	return mapper.writeValueAsString(user);        
    }

}
