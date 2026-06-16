package com.ufg.runner.assinador.exception;

import com.ufg.runner.assinador.dto.outcome.OperationOutcomeBuilder;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<OperationOutcomeDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Parâmetros inválidos");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OperationOutcomeBuilder.error(message));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<OperationOutcomeDTO> handleBusinessValidation(BusinessValidationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(OperationOutcomeBuilder.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(CryptographicException.class)
    public ResponseEntity<OperationOutcomeDTO> handleCryptographic(CryptographicException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OperationOutcomeBuilder.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationOutcomeDTO> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OperationOutcomeBuilder.error("Erro interno: " + ex.getMessage()));
    }
}
