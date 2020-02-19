package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.services.EncryptionService;
import org.springframework.context.annotation.Profile;

@Profile("no-enc")
public class NoEncryptionServiceImpl implements EncryptionService {
    @Override
    public byte[] encrypt(byte[] data) {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return data;
    }
}
