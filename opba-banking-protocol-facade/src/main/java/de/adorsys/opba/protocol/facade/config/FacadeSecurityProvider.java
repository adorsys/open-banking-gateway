package de.adorsys.opba.protocol.facade.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Getter
@AllArgsConstructor
public class FacadeSecurityProvider {
    private BouncyCastleProvider provider;
}
