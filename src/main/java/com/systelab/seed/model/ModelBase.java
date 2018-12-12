package com.systelab.seed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @JsonIgnore
    protected Timestamp creationTime;

    @LastModifiedDate
    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    @JsonIgnore
    protected Timestamp updateTime;
    
    @CreatedBy
    @Column(nullable = false, updatable = false)
    @JsonIgnore
    private String createdBy;
    
    @LastModifiedBy
    @Column(nullable = false)
    @JsonIgnore
    private String modifiedBy;

}
