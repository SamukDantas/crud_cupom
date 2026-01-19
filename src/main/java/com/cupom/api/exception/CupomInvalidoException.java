package com.cupom.api.exception;

/**
 * Exceção lançada quando dados do cupom são inválidos
 */
public class CupomInvalidoException extends RuntimeException {
    public CupomInvalidoException(String mensagem) {
        super(mensagem);
    }
}
