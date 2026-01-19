package com.cupom.api.exception;

/**
 * Exceção lançada quando um cupom não é encontrado
 */
public class CupomNaoEncontradoException extends RuntimeException {
    public CupomNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
