package com.systelab.seed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class AllergyForPatientNotFoundException extends RuntimeException {

    private final String patientId;
    private final String allergyId;

    public AllergyForPatientNotFoundException(UUID patientId, UUID allergyId) {
        super("allergy-not-found-" + patientId.toString() + "-" + allergyId);
        this.patientId = patientId.toString();
        this.allergyId = allergyId.toString();
    }

    public String getPatientId() {
        return patientId;
    }

    public String getAllergyId() {
        return allergyId;
    }
}