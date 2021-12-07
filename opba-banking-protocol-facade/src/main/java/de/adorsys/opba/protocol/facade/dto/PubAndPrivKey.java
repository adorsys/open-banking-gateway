package de.adorsys.opba.protocol.facade.dto;

import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Public and private key tuple.
 */
@Data
public class PubAndPrivKey {

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
}
