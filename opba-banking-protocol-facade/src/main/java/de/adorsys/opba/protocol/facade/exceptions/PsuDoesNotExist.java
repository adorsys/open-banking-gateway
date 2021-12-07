package de.adorsys.opba.protocol.facade.exceptions;

/**
 * An exception indicating i.e. user does not exists.
 */
public class PsuDoesNotExist extends RuntimeException {

    public PsuDoesNotExist(String message) {
        super(message);
    }
}
