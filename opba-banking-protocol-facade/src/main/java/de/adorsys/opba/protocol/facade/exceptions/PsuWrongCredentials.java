package de.adorsys.opba.protocol.facade.exceptions;

/**
 * An exception indicating user has provided wrong credentials
 */
public class PsuWrongCredentials extends Exception {
    public PsuWrongCredentials(String message, Throwable cause) {
        super(message, cause);
    }
}
