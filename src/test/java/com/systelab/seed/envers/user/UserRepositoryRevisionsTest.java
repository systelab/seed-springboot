package com.systelab.seed.envers.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.systelab.seed.core.audit.AuditRevisionEntity;
import com.systelab.seed.envers.helper.AuthenticationHelper;
import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.model.UserRole;
import com.systelab.seed.features.user.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class UserRepositoryRevisionsTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    public void save() throws JsonParseException, JsonMappingException, IOException {

        AuthenticationHelper.mockAdminAuthentication();
        user = repository.save(new User("login", "name", "surname", "password", UserRole.USER));
    }

    @Test
    public void initialRevision() {

        Revisions<Integer, User> revisions = repository.findRevisions(user.getId());

        assertThat(revisions).isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity())
                        .extracting(User::getId, User::getLogin, User::getName, User::getSurname, User::getPassword, User::getRole)
                        .containsExactly(user.getId(), user.getLogin(), user.getName(), user.getSurname(), user.getPassword(), user.getRole()))
                .allSatisfy(revision -> {
                    DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                    AuditRevisionEntity revisionEntity = metadata.getDelegate();
                    assertThat(revisionEntity.getUsername()).isEqualTo("admin");
                });
    }
}
