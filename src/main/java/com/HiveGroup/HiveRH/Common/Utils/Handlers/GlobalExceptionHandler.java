package com.HiveGroup.HiveRH.Common.Utils.Handlers;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        error.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setDetail(e.getMessage());

        error.setProperty("entity", e.getEntity());
        error.setProperty("timestamp", e.getTimestamp());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> badRequest(IllegalArgumentException e, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        error.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setDetail(e.getMessage());

        error.setProperty("timestamp", Instant.now());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> accessDenied(AccessDeniedException e, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        error.setTitle(HttpStatus.FORBIDDEN.getReasonPhrase());
        error.setDetail(e.getMessage());

        error.setProperty("timestamp", Instant.now());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> authentication(AuthenticationException e, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        error.setTitle(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        error.setDetail(e.getMessage());

        error.setProperty("timestamp", Instant.now());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> dataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        error.setTitle(HttpStatus.CONFLICT.getReasonPhrase());
        error.setDetail("Los datos ingresados entran en conflicto con un registro existente");

        error.setProperty("timestamp", Instant.now());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        error.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setDetail(errors);
        error.setProperty("timestamp", Instant.now());
        error.setProperty("path", request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
