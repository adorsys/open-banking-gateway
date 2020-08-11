package de.adorsys.opba.fintech.impl.service.oauth2;

import lombok.Data;

import java.net.URI;

@Data
public class Oauth2AuthResult {

    private final String state;
    private final URI redirectTo;
}
