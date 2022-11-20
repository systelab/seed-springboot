package com.systelab.seed.features.allergy.model;

import com.systelab.seed.core.model.ModelBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @Schema(description = "Allergy name", example = "Kiwi")
    public String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Objective evidence of disease", example = "Bloody nose")
    public String signs;

    @Size(min = 1, max = 255)
    @Schema(description = "Subjective evidence of disease", example = "Anxiety, pain and fatigue")
    public String symptoms;

}