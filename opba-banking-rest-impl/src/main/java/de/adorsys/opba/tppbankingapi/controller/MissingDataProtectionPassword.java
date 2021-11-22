package de.adorsys.opba.tppbankingapi.controller;

public class MissingDataProtectionPassword extends RuntimeException {

    public MissingDataProtectionPassword() {
        super("Missing either 'Service-Session-Password' or 'Fintech-Data-Password' header with data protection password");
    }
}
