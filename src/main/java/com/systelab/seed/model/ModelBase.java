package com.systelab.seed.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;
import java.sql.Timestamp;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class ModelBase {

    @Id
    @ApiModelProperty(notes = "The database generated model ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    protected UUID id;

    @CreationTimestamp
    @XmlTransient
    protected Timestamp creationTime;

    @UpdateTimestamp
    @XmlTransient
    protected Timestamp updateTime;

}
