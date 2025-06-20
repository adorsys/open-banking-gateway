package de.adorsys.opba.fintech.impl.service.exceptions;

public class InvalidIbanException extends RuntimeException {
    public InvalidIbanException(String message) {
        super(message);
    }
}
