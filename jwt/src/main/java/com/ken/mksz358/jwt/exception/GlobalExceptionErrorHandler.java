package com.ken.mksz358.jwt.exception;

import com.ken.mksz358.jwt.entity.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionErrorHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<JwtResponse> error(SecurityException e) {
        log.warn("occur Security Exception, {}", e);
        return new ResponseEntity<>(
                new JwtResponse(HttpStatus.UNAUTHORIZED.value(), "token validated"),
                HttpStatus.UNAUTHORIZED
        );
    }
}
