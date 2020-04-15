package de.adorsys.opba.protocol.api.services.scoped;

public interface RequestScopedServicesProvider {

    RequestScoped current();
    RequestScoped byEncryptionKeyId(String keyId);
}
