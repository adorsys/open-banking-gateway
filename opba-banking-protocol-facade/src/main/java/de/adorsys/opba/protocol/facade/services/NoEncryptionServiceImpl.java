package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoEncryptionServiceImpl implements EncryptionService {

    @Override
    public String id() {
        return "NOOP";
    }

    @Override
    public byte[] encrypt(byte[] data) {
        log.warn("No encryption implementation of encryption service is used");
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        log.warn("No encryption implementation of encryption service is used");
        return data;
    }
}
