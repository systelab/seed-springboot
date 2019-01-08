package com.systelab.seed.model.user;

import com.systelab.seed.model.ModelBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
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