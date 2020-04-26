package de.adorsys.opba.protocol.api.services.scoped.transientdata;

/**
 * Protocol-facing transient data storage (in-memory only and evictable after timeout) to provide access
 * to sensitive data like PSU password or SCA challenge result.
 */
public interface TransientStorage {

    /**
     * Retrieve current transient object value
     * @param <T> Transient object type
     * @return Transient object value (i.e. object that encapsulates password and SCA challenge result)
     */
    <T> T get();

    /**
     * Set current transient data entry.
     * @param entry Transient object value (i.e. object that encapsulates password and SCA challenge result)
     */
    void set(Object entry);
}
