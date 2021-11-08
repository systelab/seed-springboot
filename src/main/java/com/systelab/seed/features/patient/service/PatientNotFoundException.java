package com.systelab.seed.features.patient.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {

    private final String id;

    public PatientNotFoundException(UUID id) {
        super("patient-not-found-" + id.toString());
        this.id = id.toString();
    }

    public String getPatientId() {
        return id;
    }
}