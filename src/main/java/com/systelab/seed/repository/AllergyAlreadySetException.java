package com.systelab.seed.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AllergyAlreadySetException extends RuntimeException {

    private final String id;

    public AllergyAlreadySetException(UUID id) {
        super("allergy-already-set-" + id.toString());
        this.id = id.toString();
    }

    public String getAllergyId() {
        return id;
    }
}