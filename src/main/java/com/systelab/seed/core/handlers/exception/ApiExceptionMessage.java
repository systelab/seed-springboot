package com.systelab.seed.core.handlers.exception;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class ApiExceptionMessage {

    private String code;
    private HttpStatus status;
    private String originalExceptionMessage;
    private List<String> messages;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private ZonedDateTime timestamp;

    public ApiExceptionMessage(HttpStatus status, String code, String originalExceptionMessage, List<String> messages) {
        super();
        this.status = status;
        this.code = code;
        this.originalExceptionMessage = originalExceptionMessage;
        this.messages = messages;
        this.timestamp = ZonedDateTime.now();
    }

    public ApiExceptionMessage(HttpStatus status, String code, String originalExceptionMessage, String message) {
        super();
        this.status = status;
        this.code = code;
        this.originalExceptionMessage = originalExceptionMessage;
        messages = Collections.singletonList(message);
        this.timestamp = ZonedDateTime.now();
    }
}
