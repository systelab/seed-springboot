package com.systelab.seed.patient.allergy.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.systelab.seed.patient.model.Patient;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.systelab.seed.infrastructure.ModelBase;
import com.systelab.seed.allergy.model.Allergy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
public class PatientAllergy extends ModelBase {

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @ApiModelProperty(required = true)
    private Patient patient;

    @ManyToOne
    @JoinColumn
    @ApiModelProperty(required = true)
    private Allergy allergy;

    @ApiModelProperty(value = "Last date when the person hast the symptoms", example = "2018-05-14", required = true)
    private LocalDate lastOccurrence;
    @ApiModelProperty(value = "Date when the allergy was verified", example = "2007-03-23", required = true)
    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    @NotNull
    @ApiModelProperty(value = "Relevant notes to take into consideration", example = "Some notes", required = true)
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy, String note) {
        this.patient = patient;
        this.allergy = allergy;
        this.note = note;
    }

}
