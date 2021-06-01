package com.systelab.seed.patient.allergy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.systelab.seed.allergy.model.Allergy;
import com.systelab.seed.infrastructure.ModelBase;
import com.systelab.seed.patient.model.Patient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited(withModifiedFlag = true)
public class PatientAllergy extends ModelBase {

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    @Schema(required = true)
    private Patient patient;

    @ManyToOne
    @JoinColumn
    @Schema(required = true)
    private Allergy allergy;

    @Schema(description = "Last date when the person hast the symptoms", required = true, example = "2018-05-14")

    private LocalDate lastOccurrence;
    @Schema(description = "Date when the allergy was verified", required = true, example = "2007-03-23")

    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    @NotNull
    @Schema(description = "Relevant notes to take into consideration", required = true, example = "Some notes")
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy, String note) {
        this.patient = patient;
        this.allergy = allergy;
        this.note = note;
    }

}
