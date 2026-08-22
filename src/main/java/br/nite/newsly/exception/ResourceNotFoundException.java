package br.nite.newsly.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " não encontrado(a) com id: " + id);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " não encontrado(a) com " + field + ": " + value);
    }
}
