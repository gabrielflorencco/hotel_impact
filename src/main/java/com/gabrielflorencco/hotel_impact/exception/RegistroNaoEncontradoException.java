package com.gabrielflorencco.hotel_impact.exception;

/**
 * Recurso solicitado não existe (identificador ou código inválido).
 */
public class RegistroNaoEncontradoException extends RuntimeException {

    public RegistroNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}