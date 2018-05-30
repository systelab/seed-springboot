package com.systelab.model.patient;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
@XmlType(propOrder = {"id", "name", "surname", "email", "dob", "address"})

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient")
public class Patient implements Serializable {

    @Id
    @GeneratedValue
    @ApiModelProperty(notes = "The database generated patient ID")
    private Long id;

    @Size(min = 1, max = 255)
    private String surname;

    @Size(min = 1, max = 255)
    private String name;

    private String email;

    private Date dob;

    @Embedded
    private Address address;

}