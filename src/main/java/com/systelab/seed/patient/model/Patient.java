package com.systelab.seed.patient.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.systelab.seed.infrastructure.model.ModelBase;
import com.systelab.seed.patient.allergy.model.PatientAllergy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient")
public class Patient extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 255)
    private String medicalNumber;

    private String email;

    @Schema(description = "Date of Birth", example = "1966-11-17")
    private LocalDate dob;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private Set<PatientAllergy> allergies = new HashSet<PatientAllergy>();

}