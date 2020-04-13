package de.adorsys.opba.protocol.api.services;

public interface ProtocolFacingEncryptionServiceProvider {

    EncryptionService getEncryptionById(String id);
    void remove(EncryptionService service);
}
