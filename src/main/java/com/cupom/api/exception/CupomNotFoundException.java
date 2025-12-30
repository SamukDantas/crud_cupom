package com.cupom.api.exception;

/**
 * Exceção lançada quando um cupom não é encontrado
 */
public class CupomNotFoundException extends RuntimeException {
    public CupomNotFoundException(String message) {
        super(message);
    }
}
