package com.systelab.seed.features.patient.allergy.model;

import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.core.model.ModelBase;
import com.systelab.seed.features.patient.model.Patient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "patientallergies")
@Audited
public class PatientAllergy extends ModelBase {

    @ManyToOne
    @JoinColumn
    private Patient patient;

    @ManyToOne
    @JoinColumn
    private Allergy allergy;

    private LocalDate lastOccurrence;

    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    @NotNull
    private String note;

    public PatientAllergy(Patient patient, Allergy allergy, String note) {
        this.patient = patient;
        this.allergy = allergy;
        this.note = note;
    }

}
