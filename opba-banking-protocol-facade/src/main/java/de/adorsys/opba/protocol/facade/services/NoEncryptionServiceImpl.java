package de.adorsys.opba.protocol.facade.services;

import de.adorsys.opba.protocol.api.services.EncryptionService;

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
