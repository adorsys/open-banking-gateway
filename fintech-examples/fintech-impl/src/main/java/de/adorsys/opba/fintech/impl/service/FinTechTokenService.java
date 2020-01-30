package de.adorsys.opba.fintech.impl.service;

import org.springframework.stereotype.Service;

@Service
public class FinTechTokenService {
    private static final int TOKEN_LENGTH = 16;

    /**
     * service  to check the XSRF Token.
     *
     * @param fintechToken
     * @return true, if token is valid.
     * In this demo, every token is valid, whose length is exaclty 16.
     */
    public boolean validate(String fintechToken) {
        return fintechToken != null && fintechToken.length() == TOKEN_LENGTH;
    }
}
