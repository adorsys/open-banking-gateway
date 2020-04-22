package de.adorsys.opba.protocol.api.services.scoped.transientdata;

public interface TransientStorage {

    <T> T get();
    void set(Object entry);
}
