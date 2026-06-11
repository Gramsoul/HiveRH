package com.HiveGroup.HiveRH.Common.Utils.Handlers;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

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
}
