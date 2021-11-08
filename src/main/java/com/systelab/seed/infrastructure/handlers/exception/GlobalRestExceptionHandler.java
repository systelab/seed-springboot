package com.systelab.seed.infrastructure.handlers.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        final String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_ARGUMENT_TYPE_MISMATCH", ex.getLocalizedMessage(), message);
        log.warn("ERR_ARGUMENT_TYPE_MISMATCH - [{}].", message, ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        final List<String> messages = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            messages.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_CONSTRAINT_VIOLATION", ex.getLocalizedMessage(), messages);
        log.warn("ERR_CONSTRAINT_VIOLATION - [{}].", messages, ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityConstraintViolation(final DataIntegrityViolationException ex, final WebRequest request) {
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_CONSTRAINT_VIOLATION", ex.getLocalizedMessage(), "Constraint Violation");
        log.warn("ERR_CONSTRAINT_VIOLATION - [{}].", ex.getMessage(), ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleBadCredentials(final AccessDeniedException ex, final WebRequest request) {
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.FORBIDDEN, "ERR_ACCESS_DENIED", ex.getLocalizedMessage(), "Access denied");
        log.warn("ERR_ACCESS_DENIED - [{}].", ex.getMessage(), ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(final BadCredentialsException ex, final WebRequest request) {
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.FORBIDDEN, "ERR_BAD_CREDENTIALS", ex.getLocalizedMessage(), "Bad credentials");
        log.warn("ERR_BAD_CREDENTIALS_VIOLATION - [{}].", ex.getMessage(), ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler({MailSendException.class})
    public ResponseEntity<Object> handleMail(final MailSendException ex, final WebRequest request) {
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_MAIL", ex.getLocalizedMessage(), "Error sending mail");
        log.warn("ERR_MAIL - [{}].", ex.getMessage(), ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler({AuthorizationServiceException.class})
    public ResponseEntity<Object> handleAuthoritzation(final AuthorizationServiceException ex, final WebRequest request) {
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.UNAUTHORIZED, "ERR_UNAUTHORIZED", ex.getLocalizedMessage(), "Unauthorized");
        log.warn("ERR_UNAUTHORIZED - [{}].", ex.getMessage(), ex);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleAllExceptions(final Exception ex, final WebRequest request) {
        String key= getKeyForException(ex);
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(getStatusForException(ex), key, ex.getLocalizedMessage(), getHumanReadeableTextForException(ex));
        log.warn("[{}] - [{}].", key, ex.getMessage(), ex);
        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    private HttpStatus getStatusForException(final Exception ex) {
        HttpStatus status= HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getClass().isAnnotationPresent(ResponseStatus.class)) {
            status = ex.getClass().getAnnotation(ResponseStatus.class).value();
        }
        return status;
    }

    private String getKeyForException(final Exception ex) {
        String key= "ERR_GENERAL";
        if (ex.getClass().isAnnotationPresent(ApiExceptionCode.class)) {
            key = ex.getClass().getAnnotation(ApiExceptionCode.class).value();
        }
        return key;
    }

    private String getHumanReadeableTextForException(final Exception ex) {
        String text= "ERR_GENERAL";
        if (ex.getClass().isAnnotationPresent(ApiExceptionCode.class)) {
            text = ex.getClass().getAnnotation(ApiExceptionCode.class).text();
        }
        return text;
    }


}