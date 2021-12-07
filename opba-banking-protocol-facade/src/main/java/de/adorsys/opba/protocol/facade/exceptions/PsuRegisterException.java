package de.adorsys.opba.protocol.facade.exceptions;

/**
 * An exception indicating failure registering user.
 */
public class PsuRegisterException extends RuntimeException {

    public PsuRegisterException(String message) {
        super(message);
    }
}
