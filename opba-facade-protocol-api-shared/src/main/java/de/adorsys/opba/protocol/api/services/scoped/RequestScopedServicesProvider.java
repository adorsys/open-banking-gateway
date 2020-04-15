package de.adorsys.opba.protocol.api.services.scoped;

public interface RequestScopedServicesProvider {

    RequestScoped byEncryptionKeyId(String keyId);
}
