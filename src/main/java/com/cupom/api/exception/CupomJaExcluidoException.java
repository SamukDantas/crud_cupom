package com.cupom.api.exception;

/**
 * Exceção lançada quando tenta deletar um cupom já deletado
 */
public class CupomJaExcluidoException extends RuntimeException {
    public CupomJaExcluidoException(String mensagem) {
        super(mensagem);
    }
}
