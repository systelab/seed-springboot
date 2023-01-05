package com.systelab.seed.envers.user;

import com.systelab.seed.envers.helper.AuthenticationExtension;
import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.model.UserRole;
import com.systelab.seed.features.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
@ExtendWith({SpringExtension.class, AuthenticationExtension.class})
@Sql(scripts = {"classpath:sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {


    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    public void save() {
        repository.deleteAll();;
        user = repository.save(new User("surname", "name", "login", "password", UserRole.USER));
        repository.flush();
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void findAllUsers() {
        List<User> users = repository.findAll();
        assertThat(users).isNotEmpty()
                .extracting(User::getSurname, User::getName, User::getLogin, User::getPassword, User::getRole)
                .contains(tuple("surname", "name", "login", "password", UserRole.USER));
    }

    @Test
    @WithMockUser(username = "admin", roles = "MANAGER")
    void hasAuditInformation() {
        assertThat(user).extracting(User::getCreatedBy, User::getCreationTime, User::getModifiedBy, User::getModificationTime, User::getVersion)
                .isNotNull();
    }
}