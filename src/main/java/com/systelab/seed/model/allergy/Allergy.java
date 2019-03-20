package com.systelab.seed.model.allergy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.systelab.seed.model.ModelBase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allergy")
public class Allergy extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    public String name;

    @NotNull
    @Size(min = 1, max = 255)
    public String signs;

    @Size(min = 1, max = 255)
    public String symptoms;

}