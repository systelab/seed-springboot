package com.systelab.seed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
    @JsonIgnore
    protected Timestamp creationTime;

    @UpdateTimestamp
    @JsonIgnore
    protected Timestamp updateTime;

}
