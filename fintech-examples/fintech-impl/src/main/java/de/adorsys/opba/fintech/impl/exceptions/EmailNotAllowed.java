package de.adorsys.opba.fintech.impl.exceptions;

public class EmailNotAllowed extends IllegalStateException {

    public EmailNotAllowed(String msg) {
        super(msg);
    }
}
