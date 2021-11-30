package com.systelab.seed.core.handlers.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<String> messages = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            messages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            messages.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_INVALID_ARGUMENT", ex.getLocalizedMessage(), messages);
        final Object[] parameters = {HttpStatus.BAD_REQUEST, ex};
        log.warn("ERR_INVALID_ARGUMENT - BAD_REQUEST [{}].", parameters);

        return handleExceptionInternal(ex, apiExceptionMessage, headers, apiExceptionMessage.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<String> messages = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            messages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            messages.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_INVALID_BINDING", ex.getLocalizedMessage(), messages);
        final Object[] parameters = {HttpStatus.BAD_REQUEST, ex};
        log.warn("ERR_INVALID_BINDING - BAD_REQUEST [{}].", parameters);

        return handleExceptionInternal(ex, apiExceptionMessage, headers, apiExceptionMessage.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String message = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_INVALID_TYPE", ex.getLocalizedMessage(), message);
        final Object[] parameters = {HttpStatus.BAD_REQUEST, ex};
        log.warn("ERR_INVALID_TYPE - BAD_REQUEST [{}].", parameters);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String message = ex.getRequestPartName() + " part is missing";
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_PART_MISSING", ex.getLocalizedMessage(), message);
        final Object[] parameters = {HttpStatus.BAD_REQUEST, ex};
        log.warn("ERR_PART_MISSING - BAD_REQUEST [{}].", parameters);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String message = ex.getParameterName() + " parameter is missing";
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.BAD_REQUEST, "ERR_PARAMETER_MISSING", ex.getLocalizedMessage(), message);
        final Object[] parameters = {HttpStatus.BAD_REQUEST, ex};
        log.warn("ERR_PARAMETER_MISSING - BAD_REQUEST [{}].", parameters);

        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final String message = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.NOT_FOUND, "ERR_NO_HANDLER_FOUND", ex.getLocalizedMessage(), message);
        final Object[] parameters = {HttpStatus.NOT_FOUND, ex};
        log.warn("ERR_NO_HANDLER_FOUND - NOT_FOUND [{}].", parameters);
        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.METHOD_NOT_ALLOWED, "ERR_HTTP_METHOD_NOT_SUPPORTED", ex.getLocalizedMessage(), builder.toString());
        final Object[] parameters = {HttpStatus.METHOD_NOT_ALLOWED, ex};
        log.warn("ERR_HTTP_METHOD_NOT_SUPPORTED - METHOD_NOT_ALLOWED [{}].", parameters);
        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        final ApiExceptionMessage apiExceptionMessage = new ApiExceptionMessage(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "ERR_UNSUPPORTED_MEDIA_TYPE", ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
        final Object[] parameters = {HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex};
        log.warn("ERR_UNSUPPORTED_MEDIA_TYPE - UNSUPPORTED_MEDIA_TYPE [{}].", parameters);
        return new ResponseEntity<>(apiExceptionMessage, new HttpHeaders(), apiExceptionMessage.getStatus());
    }
}
