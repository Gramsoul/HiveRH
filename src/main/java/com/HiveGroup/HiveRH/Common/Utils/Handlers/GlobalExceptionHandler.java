package com.HiveGroup.HiveRH.Common.Utils.Handlers;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFound(EntityNotFoundException e, HttpServletRequest request){
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        error.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setDetail(e.getMessage());

        error.setProperty("entity", e.getEntity());
        error.setProperty("timestamp", e.getTimestamp());
        error.setProperty("path",request.getRequestURI());
        error.setProperty("method", request.getMethod());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
