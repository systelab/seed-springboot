package com.systelab.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {
    private final Long id;

    public PatientNotFoundException(Long id) {
        super("patient-not-found-" + id);
        this.id = id;
    }

    public Long getPatientId() {
        return id;
    }
}