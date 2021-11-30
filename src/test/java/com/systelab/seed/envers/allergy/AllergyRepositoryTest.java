package com.systelab.seed.envers.allergy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.util.List;

import com.systelab.seed.features.allergy.repository.AllergyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.systelab.seed.core.config.RepositoryConfig;
import com.systelab.seed.core.audit.SpringSecurityAuditorAware;
import com.systelab.seed.features.allergy.model.Allergy;

@DataJpaTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = { SpringSecurityAuditorAware.class, RepositoryConfig.class }))
@ExtendWith(SpringExtension.class)
public class AllergyRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AllergyRepository repository;

    private Allergy allergy;

    @BeforeEach
    public void save() {
        allergy = em.persistAndFlush(new Allergy("the allergy", "the signs", "the symptoms"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void findAllAllergies() {
        List<Allergy> allergies = repository.findAll();
        assertThat(allergies).isNotEmpty().extracting(Allergy::getName, Allergy::getSigns).containsExactly(tuple("the allergy", "the signs"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void hasAuditInformation() {
        assertThat(allergy)
                .extracting(Allergy::getCreatedBy, Allergy::getCreationTime, Allergy::getModifiedBy, Allergy::getModificationTime, Allergy::getVersion)
                .isNotNull();
    }
}