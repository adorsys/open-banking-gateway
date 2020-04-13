package de.adorsys.opba.protocol.facade.config.encryption;

import lombok.Data;

import javax.crypto.SecretKey;

@Data
public class SecretKeyWithIv {

    private final byte[] iv;
    private final SecretKey secretKey;
}
