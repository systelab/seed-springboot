package com.systelab.seed.envers.allergy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import com.systelab.seed.features.allergy.repository.AllergyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.features.allergy.model.Allergy;

@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AllergyRepositoryTest {

    @Autowired
    private AllergyRepository repository;

    private Allergy allergy;

    @BeforeEach
    public void save() {
        repository.deleteAll();
        allergy = repository.save(new Allergy("the allergy", "the signs", "the symptoms"));
        repository.flush();

    }


    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void findAllAllergies() {
        List<Allergy> allergies = repository.findAll();
        assertThat(allergies).isNotEmpty().extracting(Allergy::getName, Allergy::getSigns).containsExactly(tuple("the allergy", "the signs"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void hasAuditInformation() {
        assertThat(allergy)
                .extracting(Allergy::getCreatedBy, Allergy::getCreationTime, Allergy::getModifiedBy, Allergy::getModificationTime, Allergy::getVersion)
                .isNotNull();
    }
}