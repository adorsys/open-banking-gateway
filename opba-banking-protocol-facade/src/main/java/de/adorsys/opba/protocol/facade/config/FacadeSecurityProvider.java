package de.adorsys.opba.protocol.facade.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Provider;

@Getter
@AllArgsConstructor
public class FacadeSecurityProvider {

    private Provider provider;
}
