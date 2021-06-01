package com.systelab.seed.patient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;

@Data
@Audited(withModifiedFlag = true)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    private String coordinates;
    private String street;
    private String city;
    private String zip;
}
