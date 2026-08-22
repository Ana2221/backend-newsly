package br.nite.newsly.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Corpo padrão de resposta para erros da API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp,
        List<FieldError> errors
) {

    public record FieldError(
            String field,
            String message
    ) {}

    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now(), null);
    }

    public static ErrorResponse ofValidation(int status, String error, String message, List<FieldError> errors) {
        return new ErrorResponse(status, error, message, LocalDateTime.now(), errors);
    }
}
