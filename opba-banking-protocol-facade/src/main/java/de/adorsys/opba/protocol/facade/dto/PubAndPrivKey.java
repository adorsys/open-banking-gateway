package de.adorsys.opba.protocol.facade.dto;

import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
public class PubAndPrivKey {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
}
