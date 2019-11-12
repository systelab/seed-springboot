package com.systelab.seed.user.model;

import com.systelab.seed.infrastructure.ModelBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Entity
@Audited
@Table(name = "users")
public class User extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_surname")
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_name")
    private String name;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "user_login", length = 10, nullable = false, unique = true)
    private String login;

    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "user_password", length = 256, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    @NotNull
    private UserRole role;

    public User() {
        this.role = UserRole.USER;
    }

    public User(UUID id, String name, String surname, String login, String password) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = UserRole.USER;
    }
}