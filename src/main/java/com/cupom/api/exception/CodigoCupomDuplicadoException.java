package com.cupom.api.exception;

/**
 * Exceção lançada quando tenta criar cupom com código duplicado
 */
public class CodigoCupomDuplicadoException extends RuntimeException {
    public CodigoCupomDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
