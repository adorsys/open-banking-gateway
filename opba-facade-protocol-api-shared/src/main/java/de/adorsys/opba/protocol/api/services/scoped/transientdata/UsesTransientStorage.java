package de.adorsys.opba.protocol.api.services.scoped.transientdata;

/**
 * Protocol facing transient storage access object.
 */
public interface UsesTransientStorage {

    /**
     * Get access to transient storage operations.
     */
    TransientStorage transientStorage();
}
