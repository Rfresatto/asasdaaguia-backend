package br.com.aguiabranca.asasdaaguia.common.exception;

public class NegocioException extends RuntimeException {
    public NegocioException(String mensagem) {
        super(mensagem);
    }
}