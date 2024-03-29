package com.systelab.seed.features.patient.model;

import com.systelab.seed.core.model.ModelBase;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients")
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

    private LocalDate dob;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch= FetchType.EAGER)
    private Set<PatientAllergy> allergies = new HashSet<>();

}