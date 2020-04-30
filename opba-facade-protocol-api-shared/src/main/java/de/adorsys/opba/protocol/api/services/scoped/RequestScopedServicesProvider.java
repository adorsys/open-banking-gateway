package de.adorsys.opba.protocol.api.services.scoped;

/**
 * Protocol facing access to general services that are required for protocol execution.
 */
public interface RequestScopedServicesProvider {

    /**
     * Get general services that are associated with encryption key.
     * @param keyId Key ID that is used for intermediate data encryption.
     * @return Services to access data
     */
    RequestScoped findRegisteredByKeyId(String keyId);
}
