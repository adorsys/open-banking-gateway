package de.adorsys.opba.fintech.impl.exceptions;

public class Oauth2UnauthorizedException extends IllegalStateException {

    public Oauth2UnauthorizedException(String msg) {
        super(msg);
    }
}
