package br.nite.newsly.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("[404 NOT FOUND] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoStaticResource(NoResourceFoundException ex) {
        log.warn("[404 NOT FOUND] Recurso estático não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", "Recurso não encontrado."));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("[409 CONFLICT] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        log.warn("[400 BAD REQUEST] Erro de validação: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.ofValidation(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                        "Erro de validação nos campos enviados", fieldErrors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[400 BAD REQUEST] Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("[500 INTERNAL SERVER ERROR] Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                        "Ocorreu um erro interno. Tente novamente mais tarde."));
    }
}
