package com.cupom.api.exception;

/**
 * Exceção lançada quando tenta deletar um cupom já deletado
 */
public class CupomAlreadyDeletedException extends RuntimeException {
    public CupomAlreadyDeletedException(String message) {
        super(message);
    }
}
