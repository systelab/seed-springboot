package com.systelab.seed.repository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private final String id;

    public UserNotFoundException(UUID id) {
        super("user-not-found-" + id.toString());
        this.id = id.toString();
    }

    public String getUserId() {
        return id;
    }


}
