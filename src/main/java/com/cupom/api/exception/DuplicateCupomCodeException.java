package com.cupom.api.exception;

/**
 * Exceção lançada quando tenta criar cupom com código duplicado
 */
public class DuplicateCupomCodeException extends RuntimeException {
    public DuplicateCupomCodeException(String message) {
        super(message);
    }
}
