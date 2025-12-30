package com.cupom.api.exception;

/**
 * Exceção lançada quando dados do cupom são inválidos
 */
public class InvalidCupomException extends RuntimeException {
    public InvalidCupomException(String message) {
        super(message);
    }
}
