package com.systelab.seed.model.user;

import com.systelab.seed.model.ModelBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@XmlRootElement
public class User extends ModelBase {

    @Size(min = 1, max = 255)
    @Column(name = "user_surname")
    private String surname;

    @Size(min = 1, max = 255)
    @Column(name = "user_name")
    private String name;

    @Size(min = 1, max = 10)
    @Column(name = "user_login",length = 10, nullable = false, unique = true)
    private String login;

    @Size(min = 1, max = 256)
    @Column(name = "user_password",length = 256, nullable = false)
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