package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String PATH = "path";
    private static final String REASONS = "reasons";

    @ExceptionHandler(value = ValidationException.class)
    protected ResponseEntity<Object> handleNotFound(ValidationException ex, WebRequest request) {
        log.error("Not found error: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.NOT_FOUND, request);
        body.put(REASONS, ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleNotFoundException(NotFoundException e, WebRequest request) {
        log.error("404 {}", e.getMessage(), e);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.NOT_FOUND, request);
        body.put(REASONS, e.getMessage());
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("Constraint error: {}", ex.getMessage(), ex);
        Map<String, Object> body = getGeneralErrorBody(HttpStatus.BAD_REQUEST, request);
        List<String> errors = Arrays.stream(ex.getMessage().split(", ")).collect(toList());
        body.put(REASONS, errors);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        Map<String, Object> body = getGeneralErrorBody(status, request);
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::getErrorString).collect(toList());
        body.put(REASONS, errors);
        log.error(errors.toString());
        return new ResponseEntity<>(body, headers, status);
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status,
                                                    WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, status.getReasonPhrase());
        body.put(PATH, getRequestURI(request));
        return body;
    }

    private String getErrorString(ObjectError error) {
        if (error instanceof FieldError) {
            return ((FieldError) error).getField() + ' ' + error.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private String getRequestURI(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest requestHttp = ((ServletWebRequest) request).getRequest();
            return String.format("%s %s", requestHttp.getMethod(), requestHttp.getRequestURI());
        } else {
            return "";
        }
    }
}
