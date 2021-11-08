package com.systelab.seed.allergy.model;

import com.systelab.seed.infrastructure.model.ModelBase;
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
@Table(name = "allergy")
public class Allergy extends ModelBase {

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Allergy name", required = true, example = "Kiwi")
    public String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Objective evidence of disease", required = true, example = "Bloody nose")
    public String signs;

    @Size(min = 1, max = 255)
    @Schema(description = "Subjective evidence of disease", required = true, example = "Anxiety, pain and fatigue")
    public String symptoms;

}