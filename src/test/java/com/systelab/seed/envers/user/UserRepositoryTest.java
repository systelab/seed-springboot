package com.systelab.seed.envers.user;

import com.systelab.seed.core.audit.SpringSecurityAuditorAware;
import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.model.UserRole;
import com.systelab.seed.features.user.repository.UserRepository;
import com.systelab.seed.core.config.RepositoryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;


@DataJpaTest(includeFilters = @Filter(type = ASSIGNABLE_TYPE, classes = {SpringSecurityAuditorAware.class, RepositoryConfig.class}))
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    public void save() {
        user = em.persistAndFlush(new User("surname", "name", "login", "password", UserRole.USER));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void findAllUsers() {
        List<User> users = repository.findAll();
        assertThat(users).isNotEmpty()
                .extracting(User::getSurname, User::getName, User::getLogin, User::getPassword, User::getRole)
                .contains(tuple("surname", "name", "login", "password", UserRole.USER));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    public void hasAuditInformation() {
        assertThat(user).extracting(User::getCreatedBy, User::getCreationTime, User::getModifiedBy, User::getModificationTime, User::getVersion)
                .isNotNull();
    }
}