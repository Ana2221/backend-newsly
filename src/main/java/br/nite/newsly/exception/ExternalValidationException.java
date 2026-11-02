package br.nite.newsly.exception;

public class ExternalValidationException extends RuntimeException {

    public ExternalValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
