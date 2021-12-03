package de.adorsys.opba.protocol.facade.exceptions;

/**
 * An exception indicating no protocol was configured for this ASPSP and action in database.
 */
public class NoProtocolRegisteredException extends RuntimeException {

    public NoProtocolRegisteredException(String message) {
        super(message);
    }
}
