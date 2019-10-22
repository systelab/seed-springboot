package com.systelab.seed.model.allergy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Allergy name", example = "Kiwi", required = true)
    public String name;

    @NotNull
    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "Objective evidence of disease", example = "Bloody nose", required = true)
    public String signs;

    @Size(min = 1, max = 255)
    @ApiModelProperty(value = "Subjective evidence of disease", example = "Anxiety, pain and fatigue", required = true)
    public String symptoms;

}