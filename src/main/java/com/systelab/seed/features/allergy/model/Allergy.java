package com.systelab.seed.features.allergy.model;

import com.systelab.seed.core.model.ModelBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allergies")
public class Allergy extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    private String signs;

    @Size(min = 1, max = 255)
    private String symptoms;

}