package com.ufg.runner.assinador.exception;

public class CryptographicException extends RuntimeException {

    public CryptographicException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptographicException(String message) {
        super(message);
    }
}
