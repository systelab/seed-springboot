package com.systelab.seed.model.patient;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.systelab.seed.model.ModelBase;
import com.systelab.seed.model.allergy.Allergy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
public class PatientAllergy extends ModelBase implements Serializable {

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Patient patient;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Allergy allergy;

    private LocalDate lastOcurrence;
    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy, String note) {
        this.patient = patient;
        this.allergy = allergy;
        this.note = note;
    }

}
