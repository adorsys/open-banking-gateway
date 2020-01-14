package de.adorsys.opba.fintech.impl.service;

import org.springframework.context.annotation.Configuration;

@Configuration
public class X_XSRF_TokenService {
    private final static int X_XSRF_TokenLENGTH = 16;

    /**
     * service  to check the XSRF Token.
     * @param X_XSRF_Token
     * @return true, if token is valid.
     * In this demo, every token is valid, whose length is exaclty 16.
     */
    public boolean validate(String X_XSRF_Token) {
        return (X_XSRF_Token != null && X_XSRF_Token.length() == X_XSRF_TokenLENGTH);
    }
}
