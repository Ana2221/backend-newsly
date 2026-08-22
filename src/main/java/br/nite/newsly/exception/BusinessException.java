package br.nite.newsly.exception;

/**
 * Exceção para violações de regras de negócio.
 * Exemplos: colaborador já é diretor, CNPJ duplicado, diretoria já possui diretor.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
